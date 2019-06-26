package com.example.awarex.Model;

public class TvShow implements Cloneable {
    public String name, img, air;

    public TvShow() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAir() {
        return air;
    }

    public void setAir(String air) {
        this.air = air;
    }


    //Object cloning
    @Override
    public Object clone() throws CloneNotSupportedException {
        TvShow clone = null;
        try
        {
            clone = (TvShow) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
        return clone;
    }
}
