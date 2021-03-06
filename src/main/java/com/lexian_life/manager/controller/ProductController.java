package com.lexian_life.manager.controller;

import com.lexian_life.aop.AdminControllerLog;
import com.lexian_life.domain.Catalog;
import com.lexian_life.domain.Category;
import com.lexian_life.domain.Goods;
import com.lexian_life.domain.GoodsAttr;
import com.lexian_life.service.CatalogService;
import com.lexian_life.service.CategoryService;
import com.lexian_life.service.GoodsAttrService;
import com.lexian_life.service.GoodsService;
import com.sun.beans.editors.DoubleEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * @author coderWu
 * Created in 下午2:41 17-9-22
 */
@EnableTransactionManagement
@Controller
@RequestMapping("/manager/product")
public class ProductController {

    @Autowired
    CategoryService categoryService;
    @Autowired
    CatalogService catalogService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    GoodsAttrService goodsAttrService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(double.class, new DoubleEditor());

    }

    @RequestMapping("/list")
    public String list(ModelMap model) {
        model.put("catalogList", getCategory());
        model.put("goods", goodsService.getAllGoods());
        return "manager/product/Products_List";
    }

    @RequestMapping("/category")
    public String category(ModelMap model) {
        model.put("catalogList", getCategory());
        return "manager/product/Category_Manage";
    }

    @RequestMapping("/product_add")
    public String productAdd(Integer id, ModelMap model) {
        if (id != null) {
            model.put("goods", goodsService.getGoodsById(id));
        } else {
            model.put("goods", new Goods());
        }
        model.put("catalogList", getCategory());
        return "manager/product/product-add";
    }

    @RequestMapping(value = "/get_goods", method = RequestMethod.POST)
    @ResponseBody
    public List<Goods> getGoods(Integer categoryId) {
        if (categoryId == null || 0 == categoryId) {
            return goodsService.getAllGoods();
        }
        List<Goods> result = goodsService.getGoodsByCategoryId(categoryId);
        return result;
    }

    private HashMap<Catalog, List<Category>> getCategory() {
        HashMap<Catalog, List<Category>> result = new HashMap<>();
        Iterable<Catalog> catalogs = catalogService.getAllCatalogs();
        for (Catalog catalog : catalogs) {
            result.put(catalog, categoryService.getAllByCatalog(catalog));
        }
        return result;
    }

    @RequestMapping(value = "/new_product_add", method = RequestMethod.POST)
    @ResponseBody
    @AdminControllerLog(description = "添加商品或修改")
    public Map<String, Object> newProduct(Goods goods, Integer categoryid, @RequestParam MultipartFile picture,
                                          @RequestParam MultipartFile bigpic[],
                                          @RequestParam MultipartFile adpic[],
                                          String[] attrname, String[] attrvalue, String[] attrid, HttpServletRequest request) {
        Map<String, Object> result = new HashMap();
        boolean status = false;
        String msg = "添加失败";
        Goods good = new Goods();
        good = newProduct(goods, categoryid);
        if (picture.getSize() <= 0 && good.getGoodsId() == 0) {
            msg = "缺少图片";
        } else if (picture.getSize() >= 0){
            String pictureName = System.currentTimeMillis() + ".jpg";
            if (picture.getSize() > 0) {
                try {
                    File file = new File(request.getServletContext().getRealPath("/img/goodsImg"), pictureName);
                    picture.transferTo(file);
                    good.setImg("../../../img/goodsImg/" + pictureName);
                    status = true;
                } catch (Exception e) {
                    status = false;
                    e.printStackTrace();
                }
            } else {
                status = true;
            }
            if (status) {
                Goods goods1 = goodsService.addGood(good);
                if (attrname != null) {
                    List<GoodsAttr> attrs = new ArrayList();
                    if (attrid != null) {
                        for (int i = 0; i < attrid.length; i++) {
                            if (attrname[i].equals("") || attrvalue[i].equals("")) {
                                goodsAttrService.deleteAttrByAttrId(Integer.parseInt(attrid[i]));
                            } else {
                                GoodsAttr goodsAttr = goodsAttrService.findById(Integer.parseInt(attrid[i]));
                                goodsAttr.setName(attrname[i]);
                                goodsAttr.setValue(attrvalue[i]);
                                goodsAttr.setGoods(goods1);
                                attrs.add(goodsAttr);
                            }
                        }
                    }
                    for (int i = attrid == null ? 0 : attrid.length, l = Math.min(attrname.length, attrvalue.length); i < l; i++) {
                        if (!attrname[i].equals("") || !attrvalue[i].equals("")) {
                            GoodsAttr attr = new GoodsAttr();
                            attr.setName(attrname[i]);
                            attr.setValue(attrvalue[i]);
                            attr.setGoods(goods1);
                            attrs.add(attr);
                        }
                    }
                    goodsAttrService.addAttrs(attrs);
                    goodsService.addBigPicture(goods1, bigpic, request);
                    goodsService.addAdPicture(goods1, adpic, request);
                }
            }
        }
        result.put("status", status);
        result.put("msg", msg);
        return result;
    }

    private Goods newProduct(Goods goods, int categoryid) {
        Goods good;
        if (goods.getGoodsId() == 0) {
            good = new Goods();
            good.setOnsaleTime(new Date());
            good.setStatus(0);
            good.setSaleCount(goods.getSaleCount());
        } else {
            good = goodsService.getGoodsById(goods.getGoodsId());
        }
        good.setWeight(goods.getWeight());
        Category category = new Category();
        category.setCategory_id(categoryid);
        good.setCategory(category);
        good.setUnitPrice(goods.getUnitPrice());
        good.setUnit(goods.getUnit());
        good.setName(goods.getName());
        good.setInventory(goods.getInventory());
        return good;
    }

    @RequestMapping("/change_status")
    @ResponseBody
    @AdminControllerLog(description = "修改商品状态")
    public Map<String, Object> changeStatus(Integer id, Boolean status) {
        Boolean success = false;
        Map<String, Object> result = new HashMap();
        if (id != null && status != null) {
            Goods goods = goodsService.getGoodsById(id);
            if (goods != null) {
                goods.setStatus(status ? 0 : 1);
                goodsService.updateGoods(goods);
                success = true;
            }
        }
        result.put("status", success);
        return result;
    }

    @RequestMapping("/delete_good")
    @ResponseBody
    @Transactional
    @AdminControllerLog(description = "删除商品")
    public Map<String, Object> deleteGoods(Integer id) {
        Map<String, Object> result = new HashMap();
        if (id != null) {
            goodsAttrService.deleteGoodsAttr(id);
            goodsService.deleteGoods(id);
        }
        result.put("status", true);
        return result;
    }

    @RequestMapping("/category_add")
    @ResponseBody
    @AdminControllerLog(description = "添加分类")
    public Map<String, Object> categoryAdd(Integer id, String name, ModelMap model) {
        Map<String, Object> result = new HashMap();
        boolean status = false;
        if (id != null && name != null && !"".equals(name)) {
            Category category = new Category();
            category.setName(name);
            Catalog catalog = new Catalog();
            catalog.setCatalog_id(id);
            category.setCatalog(catalog);
            categoryService.addCategory(category);
            status = true;
        } else {
            result.put("msg", "信息不完整");
        }
        result.put("status", status);
        return result;
    }

    @RequestMapping("/category_delete")
    @ResponseBody
    @AdminControllerLog(description = "删除分类")
    public Map<String, Object> categoryDelete(Integer id) {
        Map<String, Object> result = new HashMap();
        boolean status = false;
        if (id != null ) {
            categoryService.deleteCategory(id);
            status = true;
        } else {
            result.put("msg", "信息不完整");
        }
        result.put("status", status);
        return result;
    }

}
