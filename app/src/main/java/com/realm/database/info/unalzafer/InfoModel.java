package com.realm.database.info.unalzafer;

import io.realm.RealmObject;

public class InfoModel extends RealmObject {
    private int id;
    private  String name;
    private  String age;

    @Override
    public String toString() {
        return "Ad Soyad="+name+
                "  Yaş="+age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
