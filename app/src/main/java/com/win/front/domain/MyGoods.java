package com.win.front.domain;

public class MyGoods {
    Long number;
    String id;
    String my_goods_name;
    String my_goods_brand;
    String status;

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMy_goods_name() {
        return my_goods_name;
    }

    public void setMy_goods_name(String my_goods_name) {
        this.my_goods_name = my_goods_name;
    }

    public String getMy_goods_brand() {
        return my_goods_brand;
    }

    public void setMy_goods_brand(String my_goods_brand) {
        this.my_goods_brand = my_goods_brand;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "MyGoods{" +
                "number=" + number +
                ", id='" + id + '\'' +
                ", my_goods_name='" + my_goods_name + '\'' +
                ", my_goods_brand='" + my_goods_brand + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
