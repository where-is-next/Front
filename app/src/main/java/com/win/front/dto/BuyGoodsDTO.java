package com.win.front.dto;

public class BuyGoodsDTO {
    String id;
    String goods_name;
    String price;
    String brand;
    String status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGoods_name() {
        return goods_name;
    }

    public void setGoods_name(String goods_name) {
        this.goods_name = goods_name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "BuyGoodsDTO{" +
                "id='" + id + '\'' +
                ", goods_name='" + goods_name + '\'' +
                ", price='" + price + '\'' +
                ", brand='" + brand + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
