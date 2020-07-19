/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.util.ArrayList;
import java.util.List;
import rs.etf.sab.operations.CityOperations;

/**
 *
 * @author acamr
 */
public class ma160414_CityOperations implements CityOperations {

    public ma160414_CityOperations() {
    }

    @Override
    public int insertCity(String string, String string1) {
        return DB.insert("Grad",
                new String[]{"Naziv", "PostanskiBroj"},
                new String[]{string, string1});
    }

    @Override
    public int deleteCity(String... strings) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM [GRAD] WHERE ");
        for(String s: strings){
            sb.append("Naziv='").append(s).append("' OR ");
        }
        sb.setLength(sb.length() - 3);
        sb.append(";");
        return DB.delete(sb.toString());
    }

    @Override
    public boolean deleteCity(int i) {
        int deleted = DB.deleteAND("Grad",
                new String[]{"ID"},
                new String[]{String.valueOf(i)});
        return 1 == deleted;
    }

    @Override
    public List<Integer> getAllCities() {
        List<Integer> list = new ArrayList<>();
        List<List<String>> rs = DB.select("Grad",
                new String[]{"ID"},
                null,
                null,
                null);
        
        rs.forEach((l) -> {
            list.add(Integer.parseInt(l.get(0)));
        });
        
        return list;
    }
    
}
