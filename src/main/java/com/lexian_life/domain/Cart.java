package com.lexian_life.domain;

import javax.persistence.*;
import java.util.List;

/**
 * Created by dengxiaobing on 2017/9/21.
 */
@Entity
@Table(name = "cart")
public class Cart {
    @Column(name = "cart_id")
    @Id
    @GeneratedValue
    private int cartId;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cus_id")
    private Customer customer;
    @OneToMany(mappedBy = "cart",cascade = {CascadeType.MERGE,CascadeType.REFRESH,CascadeType.DETACH},fetch = FetchType.EAGER)
    List<CartItem> cartItems;

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
    public int selectCount(){
        int count = 0;
        for(int i=0;i<cartItems.size();i++){
            if(cartItems.get(i).getSelected()==1)count++;
        }
        return count;
    }
    public double getTotalPrice(){
        double totalPrice = 0;
        for(int i=0;i<cartItems.size();i++){
            if(cartItems.get(i).getSelected()==1) {
                totalPrice += cartItems.get(i).getGoods().getUnitPrice() * cartItems.get(i).getNum();
            }
        }
        return totalPrice;
    }
    public int getSelectSize(){
        int count = 0;
        for(int i=0;i<cartItems.size();i++){
            if(cartItems.get(i).getSelected()==1)count++;
        }
        return count;
    }
}
