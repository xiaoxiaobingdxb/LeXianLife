package com.lexian_life.web.controller;

import com.lexian_life.domain.*;
import com.lexian_life.service.CustomerService;
import com.lexian_life.service.GoodsService;
import com.lexian_life.util.ServletUitl;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by dengxiaobing on 2017/9/18.
 */
@Controller
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private CustomerService customerService;

    @RequestMapping(value = "/findGoods",method = {RequestMethod.POST,RequestMethod.GET})
    public String findGoods(String name,@RequestParam(required = false)Integer pageIndex,@RequestParam(required = false)Integer sort,HttpServletRequest request){
        if(pageIndex==null) pageIndex = 1;
        if(sort==null)sort = 0;
        Page<Goods> goodsPage = goodsService.blurryGoodsSearch(name,pageIndex,sort);
        request.setAttribute("goodsPage",goodsPage);
        List<Category> mayFindList = getMayFind(name);
        request.setAttribute("mayFindList",mayFindList);
        request.setAttribute("name",name);
        request.setAttribute("sort",sort);
        return "/foreground/product/ViewFindGoods";
    }

    private List<Category> getMayFind(String name) {
        return goodsService.blurryCategorySearch(name);
    }

    private Category getCategory(List<Catalog> catalogs,int category_id){
        Category category = null;
        for(int i=0;i<catalogs.size();i++){
            for(int j=0;j<catalogs.get(i).getCategories().size();j++){
                if(catalogs.get(i).getCategories().get(j).getCategory_id()==category_id){
                    category = catalogs.get(i).getCategories().get(j);
                    return category;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param category_id
     * @param pageIndex 页(从1开始)
     * @param sort 排序方式(0综合,1销量优先,2价格优先
     * @param request
     * @return
     */
    @RequestMapping(value = "/viewCategory",method = {RequestMethod.GET})
    public String viewCategory(int category_id, int pageIndex, @RequestParam(required = false) Integer sort, HttpServletRequest request){
        List<Catalog> catalogs = (List<Catalog>) request.getSession().getAttribute("catalogs");
        Category category = getCategory(catalogs,category_id);
//        Category category = goodsService.getCategory(category_id);
        if(sort==null)sort=0;
        if(sort==2&&request.getSession().getAttribute("sort").equals(2)){
            sort = 3;
        }
        request.getSession().setAttribute("sort",sort);
        Page<Goods> goodsPage = goodsService.getGoods(category.getCategory_id(),pageIndex-1,16,sort);
        request.setAttribute("viewCategory",category);
        request.setAttribute("page",goodsPage);
        return "/foreground/product/ViewProduct";
    }

    public String viewCategoryByPrice(int category_id,int pageIndex,@RequestParam(required = false)Integer sort,HttpServletRequest request){
        return null;
    }

    /**
     * 添加关注
     * @param goods_id
     */
    @RequestMapping(value = "/addAttention")
    public void addAttention(int goods_id, HttpSession session, HttpServletResponse response) {
        try {
            JSONObject jsonObject = new JSONObject();
            String encoding = "utf-8";
            response.setContentType("text/plain;charset=" + encoding);
            response.setCharacterEncoding(encoding);
            PrintWriter pw = response.getWriter();
            Goods goods = goodsService.findGoodsById(goods_id);
            Customer customer = (Customer) session.getAttribute("customer");
            if(customer==null){
                jsonObject.put("result",false);
                jsonObject.put("msg","请先登录");
                pw.println(jsonObject.toString());
                pw.flush();
                pw.close();
                return;
            }
            Attention attention = new Attention();
            attention.setGoods(goods);
            attention.setCustomer(customer);
            Attention getAttention = goodsService.addAttention(attention);
            if(getAttention==null) {
                jsonObject.put("result", false);
                jsonObject.put("msg","已关注该商品");
            }else{
                jsonObject.put("result",true);
                jsonObject.put("msg","添加关注成功");
            }
            pw.println(jsonObject.toString());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    @RequestMapping(value = "/viewGoods",method = {RequestMethod.GET})
    public String viewGoods(int goods_id,HttpServletRequest request,HttpServletResponse response){
        Date now = new Date();
        int monthSaleCount = goodsService.getCurMonthSaleCount(goods_id);
        Goods goods = goodsService.findGoodsById(goods_id);
        int goodCount = goodsService.getCommentCountByScore(goods_id,5)+goodsService.getCommentCountByScore(goods_id,4);
        int midCount = goodsService.getCommentCountByScore(goods_id,3)+goodsService.getCommentCountByScore(goods_id,2);
        int badCount = goodsService.getCommentCountByScore(goods_id,1)+goodsService.getCommentCountByScore(goods_id,0);
        if(request.getSession().getAttribute("customer")!=null) {//如果顾客登录了就存到数据库中
            customerService.browseGoods((Customer) request.getSession().getAttribute("customer"), goods,now);
        } else{//没登录就存到cookie中
            Cookie cookie = ServletUitl.getCookieByName(request.getCookies(),"browsedGoods");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            if(cookie!=null){//此cookie已存在
                String json = cookie.getValue();
                try {
                    JSONArray array = new JSONArray(json);
                    JSONObject obj = new JSONObject();
                    obj.put("goods_id",goods.getGoodsId());
                    obj.put("time",format.format(now));
                    array.put(obj);
                    cookie.setValue(array.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{//不存在就new一个
                try {
                    JSONArray array = new JSONArray();
                    JSONObject obj = new JSONObject();
                    obj.put("goods_id",goods.getGoodsId());
                    obj.put("time",format.format(now));
                    array.put(obj);
                    cookie = new Cookie("browsedGoods",array.toString());
                    cookie.setMaxAge(Integer.MAX_VALUE);
                    response.addCookie(cookie);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        request.setAttribute("monthSaleCount",monthSaleCount);
        request.setAttribute("goodCount",goodCount);
        request.setAttribute("midCount",midCount);
        request.setAttribute("badCount",badCount);
        request.setAttribute("viewGoods",goods);
        return "/foreground/product/ProductDetail";
    }

    /**
     * 查询商品评论
     * @param goods_id
     * @param pageIndex
     * @param response
     */
    @RequestMapping(value = "/queryComments",method = {RequestMethod.GET})
    public void queryComments(int goods_id,int pageIndex,HttpServletResponse response){
        Page<Comment> page = goodsService.getComment(goods_id,pageIndex);
        String encoding = "utf-8";
        response.setContentType("text/plain;charset=" + encoding);
        response.setCharacterEncoding(encoding);
        List<Comment> list = page.getContent();
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm");
        try {
            PrintWriter pw = response.getWriter();
            JSONObject result = new JSONObject();
            JSONArray array = new JSONArray();
            for(int i=0;i<list.size();i++){
                Comment comment = list.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name",customerService.getCustomer(comment.getCusId()).getNickname());
                jsonObject.put("time",format.format(comment.getTime()));
                jsonObject.put("content",comment.getContent());
                array.put(jsonObject);
            }
            result.put("pageIndex",pageIndex);
            result.put("pageCount",page.getTotalPages());
            result.put("comments",array);
            pw.println(result.toString());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查詢4個猜你喜歡
     * @param request
     * @param response
     */
    @RequestMapping(value = "/queryMayLike",method = {RequestMethod.GET})
    public void queryMayLike(HttpServletRequest request,HttpServletResponse response){
        String encoding = "utf-8";
        response.setContentType("text/plain;charset=" + encoding);
        response.setCharacterEncoding(encoding);
        Customer customer = (Customer) request.getSession().getAttribute("customer");
        JSONArray array  = new JSONArray();
        int count = 0;
        try {
            PrintWriter pw = response.getWriter();
            //读取顾客的浏览记录
            if(customer==null){
                Cookie cookie = ServletUitl.getCookieByName(request.getCookies(),"browsedGoods");
                if(cookie!=null){
                    JSONArray bgArray = new JSONArray(cookie.getValue());
                    for(int i=bgArray.length()-1;i>=0;i--){
                        if(count<4) {
                            JSONObject bgObj = bgArray.getJSONObject(i);
                            int goods_id = bgObj.getInt("goods_id");
                            Goods goods = goodsService.getGoodsById(goods_id);
                            JSONObject obj = new JSONObject();
                            obj.put("goods_id", goods_id);
                            obj.put("img", goods.getImg());
                            obj.put("name", goods.getName());
                            obj.put("unitPrice",goods.getUnitPrice());
                            obj.put("saleCount", goods.getSaleCount());
                            array.put(obj);
                            count++;
                        }
                    }
                }
            }else{
                List<BrowseRecord> browseRecords = customerService.getBrowserRecords(1,customer);
                for(int i=0;i<browseRecords.size();i++){
                    if(count<4){
                        Goods goods = browseRecords.get(i).getGoods();
                        JSONObject obj = new JSONObject();
                        obj.put("goods_id",goods.getGoodsId());
                        obj.put("img",goods.getImg());
                        obj.put("name",goods.getName());
                        obj.put("unitPrice",goods.getUnitPrice());
                        obj.put("saleCount",goods.getSaleCount());
                        array.put(obj);
                        count++;
                    }
                }
            }
            if(count<4){//不够4个
                List<Goods> list = goodsService.getWellSaleGoods(1,4).getContent();
                for(int i=0;i<list.size();i++){
                    if(count<4){
                        Goods goods = list.get(i);
                        JSONObject obj = new JSONObject();
                        obj.put("goods_id",goods.getGoodsId());
                        obj.put("img",goods.getImg());
                        obj.put("name",goods.getName());
                        obj.put("unitPrice",goods.getUnitPrice());
                        obj.put("saleCount",goods.getSaleCount());
                        array.put(obj);
                        count++;
                    }
                }
            }
            pw.println(array.toString());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询销量最好的
     * @param response
     */
    @RequestMapping(value = "/queryWellSale",method = {RequestMethod.GET})
    public void queryWellSale(HttpServletResponse response){
        String encoding = "utf-8";
        response.setContentType("text/plain;charset=" + encoding);
        response.setCharacterEncoding(encoding);
        try {
            PrintWriter pw = response.getWriter();
            JSONArray array = new JSONArray();
            List<Goods> list = goodsService.getWellSaleGoods(1,4).getContent();
            for(int i=0;i<list.size();i++){
                Goods goods = list.get(i);
                JSONObject obj = new JSONObject();
                obj.put("goods_id",goods.getGoodsId());
                obj.put("img",goods.getImg());
                obj.put("name",goods.getName());
                obj.put("unitPrice",goods.getUnitPrice());
                array.put(obj);
            }
            pw.println(array.toString());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

