/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rs.etf.sab.operations.PackageOperations;

/**
 *
 * @author acamr
 */
public class ma160414_PackageOperations implements PackageOperations {

    public ma160414_PackageOperations() {
    }

    @Override
    public int insertPackage(int polaziste, int odrediste, String korisnik, int tip, BigDecimal tezina) {
        CallableStatement cs = DB.call("spInsertPaket",
                polaziste, odrediste, korisnik, tip, tezina, DB.outParams.Integer);
        
        try {
            return cs.getInt(6);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }

    @Override
    public boolean acceptAnOffer(int i) {
        CallableStatement cs = DB.call("spPrihvatiPonudu",
                i, DB.outParams.Boolean);
        
        try {
            return cs.getBoolean(2);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean rejectAnOffer(int i) {
        CallableStatement cs = DB.call("spOdbijPonudu",
                i, DB.outParams.Boolean);
        
        try {
            return cs.getBoolean(2);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public List<Integer> getAllPackages() {
        List<List<String>> idString = DB.select("Paket",
                new String[]{"ID"},
                null,
                null,
                null);
        
        List<Integer> idInt = new ArrayList<>();
        
        idString.forEach((s) -> {
            idInt.add(Integer.parseInt(s.get(0)));
        });
        
        return idInt;
    }

    @Override
    public List<Integer> getAllPackagesWithSpecificType(int i) {
        List<String> idString = DB.select("Paket",
                new String[]{"ID"},
                new String[]{"Tip"},
                new String[]{String.valueOf(i)},
                null).get(0);
        
        List<Integer> idInt = new ArrayList<>();
        
        idString.forEach((s) -> {
            idInt.add(Integer.parseInt(s));
        });
        
        return idInt;
    }

    @Override
    public List<Integer> getAllUndeliveredPackages() {
        // 1 - prihvacen; 2 - preuzet
        List<List<String>> idString = DB.select("Paket",
                new String[]{"ID"},
                new String[]{"Status", "Status"},
                new String[]{String.valueOf(1), String.valueOf(2)},
                null);
        
        List<Integer> idInt = new ArrayList<>();
        
        idString.forEach((s) -> {
            idInt.add(Integer.parseInt(s.get(0)));
        });
        
        return idInt;
    }

    @Override
    public List<Integer> getAllUndeliveredPackagesFromCity(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT P.ID"
                + " FROM [Paket] P INNER JOIN [Adresa] A ON P.Polaziste=A.ID"
                + " WHERE (P.Status=1 OR P.Status=2) AND A.Grad=")
                .append(String.valueOf(i)).append(";");
        
        List<List<String>> idString = DB.select(sb.toString(), 1);
        
        List<Integer> idInt = new ArrayList<>();
        
        idString.forEach((s) -> {
            idInt.add(Integer.parseInt(s.get(0)));
        });
        
        return idInt;
    }

    @Override
    public List<Integer> getAllPackagesCurrentlyAtCity(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT P.ID"
                + " FROM [Adresa] A, [Paket] P"
                + " WHERE P.Uvozilu = 0 AND P.TrenutnaLokacija = A.ID"
                + " AND A.Grad = ").append(String.valueOf(i)).append(";");
        
        List<List<String>> idString = DB.select(sb.toString(), 1);
        
        List<Integer> idInt = new ArrayList<>();
        
        idString.forEach((s) -> {
            idInt.add(Integer.parseInt(s.get(0)));
        });
        
        return idInt;
    }

    @Override
    public boolean deletePackage(int i) {
        // 0 - kreiran; 4 - odbijen
        int deleted = DB.deleteOR("Paket",
                new String[]{"Status", "Status"},
                new String[]{String.valueOf(0), String.valueOf(4)});
        return 1 == deleted;
    }

    @Override
    public boolean changeWeight(int i, BigDecimal bd) {
        // 0 - kreiran
        return DB.update("Paket",
                new String[]{"Tezina"},
                new String[]{bd.toString()},
                new String[]{"ID", "Status"},
                new String[]{String.valueOf(i), String.valueOf(0)});
    }

    @Override
    public boolean changeType(int i, int i1) {
        // 0 - kreiran
        return DB.update("Paket",
                new String[]{"Tip"},
                new String[]{String.valueOf(i1)},
                new String[]{"ID", "Status"},
                new String[]{String.valueOf(i), String.valueOf(0)});
    }

    @Override
    public BigDecimal getPriceOfDelivery(int i) {
        List<String> list = DB.select("ZahtevPrevoz",
                new String[]{"CenaIsporuke"},
                new String[]{"Paket"},
                new String[]{String.valueOf(i)},
                null).get(0);
        
        if(list.isEmpty()) return new BigDecimal(-1);
        return new BigDecimal(list.get(0));
    }

    @Override
    public Date getAcceptanceTime(int i) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("SELECT VremePrihvatanja FROM [Paket] ");
        sb.append("WHERE ID=").append(String.valueOf(i)).append(" AND ");
        sb.append("Status=").append(String.valueOf(1)).append(";");
        
        List<String> list = DB.select(sb.toString(), 1).get(0);
        
        if(list.isEmpty()) return null;
        return Date.valueOf(list.get(0));
    }

    @Override
    public int getDeliveryStatus(int i) {
        List<String> list = DB.select("Paket",
                new String[]{"Status"},
                new String[]{"ID"},
                new String[]{String.valueOf(i)},
                null).get(0);
        
        if(list.isEmpty()) return -1;
        return Integer.parseInt(list.get(0));
    }

    @Override
    public int getCurrentLocationOfPackage(int i) {
        CallableStatement cs = DB.call("spTrenutnaLokacijaPaket",
                i, DB.outParams.Integer);
        
        try {
            return cs.getInt(2);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
}
