package com.emagroup.oversea.sdk;

/**
 * Created by beyearn on 2018/2/28.
 */

public class OrderInfo {


    /**
     * order_id : LER6OPQrdrpde
     * op_id : 2106
     * game_id : 168
     * product_id : com.emagroups.wol.40
     * product_name : Diamonds
     * price : 0.99
     * currency : USD
     * quantity : 1
     */

    private String order_id;
    private String op_id;
    private String game_id;
    private String product_id;
    private String product_name;
    private double price;
    private String currency;
    private String quantity;

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getOp_id() {
        return op_id;
    }

    public void setOp_id(String op_id) {
        this.op_id = op_id;
    }

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
