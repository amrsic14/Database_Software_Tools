/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.util.Pair;
import rs.etf.sab.operations.DriveOperation;

/**
 *
 * @author acamr
 */
public class ma160414_DriveOperation implements DriveOperation {

    private HashMap<String, Pair<Vehicle,List<Package>>> map = new HashMap<>();
    
    public ma160414_DriveOperation() {
    }

    private class Package {
        public int id;
        public int odrediste;
        public BigDecimal cenaIsporuke;
        public BigDecimal tezina;
        public BigDecimal x;
        public BigDecimal y;
        public BigDecimal startX;
        public BigDecimal startY;
        public boolean initial = true;
        public boolean magacin = true;
        public boolean zaIsporuku = true;
        public boolean isporucen = false;
        public boolean uVozilu = false;

        public Package(int id,
                int odrediste,
                BigDecimal cenaIsporuke,
                BigDecimal tezina,
                boolean initial,
                boolean magacin,
                boolean zaIsporuku) {
            this.id = id;
            this.odrediste = odrediste;
            this.cenaIsporuke = cenaIsporuke;
            this.tezina = tezina;
            this.initial = initial;
            this.magacin = magacin;
            this.zaIsporuku = zaIsporuku;
        }   
    }
    
    private class Vehicle {
        public String id;
        public int tipGoriva;
        public BigDecimal potrosnja;
        public BigDecimal nosivost;
        public BigDecimal popunjeno = new BigDecimal(0);
        public BigDecimal x;
        public BigDecimal y;
        public BigDecimal putanja = new BigDecimal(0);
        public BigDecimal cenaIsporuceno = new BigDecimal(0);

        public Vehicle(String id,
                int tipGoriva,
                BigDecimal potrosnja,
                BigDecimal nosivost,
                BigDecimal x,
                BigDecimal y) {
            this.id = id;
            this.tipGoriva = tipGoriva;
            this.potrosnja = potrosnja;
            this.nosivost = nosivost;
            this.x = x;
            this.y = y;
        }
    }
    
    private int getCityFromCords(BigDecimal x, BigDecimal y){
        List<String> id = DB.select("Adresa",
            new String[]{"Grad"},
            new String[]{"X", "Y"},
            new String[]{String.valueOf(x.doubleValue()), String.valueOf(y.doubleValue())},
            null).get(0);
        
        return Integer.parseInt(id.get(0));
    }
    
    private int getAddressFromCords(BigDecimal x, BigDecimal y){
        List<String> id = DB.select("Adresa",
            new String[]{"ID"},
            new String[]{"X", "Y"},
            new String[]{String.valueOf(x.doubleValue()), String.valueOf(y.doubleValue())},
            null).get(0);
        
        return Integer.parseInt(id.get(0));
    }
    
    private String getVehicle(String username){
        CallableStatement cs = DB.call("spDohvatiVozilo",
                username, DB.outParams.Integer, DB.outParams.String);
        
        try {
            if(1 != cs.getInt(2))
                return "-1";
            else
                return cs.getString(3);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return "-1";
        }
    }
    
    private boolean assignVehicle(String user, String vehicle){
        CallableStatement cs = DB.call("spDodeliVozilo",
                user, vehicle, DB.outParams.Integer);
        
        try {
            return 1 == cs.getInt(3);
        } catch (SQLException ex) {
            Logger.getLogger(ma160414_PackageOperations.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    private int getCityFromUser(String user){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT A.Grad"
                + " FROM [Korisnik] K, [Adresa] A"
                + " WHERE K.Adresa = A.ID"
                + " AND K.KorisnickoIme = '").append(user).append("';");
        
        List<String> id = DB.select(sb.toString(), 1).get(0);
        
        return Integer.parseInt(id.get(0));
    }
    
    private void setDestinationCords(Package p, int adr){
        List<String> cord = DB.select("Adresa",
            new String[]{"X", "Y"},
            new String[]{"ID"},
            new String[]{String.valueOf(adr)},
            null).get(0);
        
        p.x = new BigDecimal(cord.get(0));
        p.y = new BigDecimal(cord.get(1));
    }
    
    private void setStartCords(Package p, int adr){
        List<String> cord = DB.select("Adresa",
            new String[]{"X", "Y"},
            new String[]{"ID"},
            new String[]{String.valueOf(adr)},
            null).get(0);
        
        p.startX = new BigDecimal(cord.get(0));
        p.startY = new BigDecimal(cord.get(1));
    }
    
    private List<Package> getPackagesCity(int city, boolean initial, boolean magacin, boolean isporuka){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT P.ID, P.Odrediste, ZP.CenaIsporuke, P.Tezina, P.TrenutnaLokacija"
                + " FROM [ZahtevPrevoz] ZP, [Paket] P, [Adresa] A"
                + " WHERE P.ID = ZP.Paket AND P.UVozilu = 0 AND P.UMagacinu = 0 AND P.Status = 1"
                + " AND P.TrenutnaLokacija = A.ID"
                + " AND A.Grad = ").append(String.valueOf(city))
                .append(" ORDER BY P.VremePrihvatanja ASC;");
        
        List<List<String>> packages = DB.select(sb.toString(), 5);
        
        List<Package> ret = new ArrayList<>();
        
        packages.forEach((s) -> {
            int id = Integer.parseInt(s.get(0));
            int odr = Integer.parseInt(s.get(1));
            BigDecimal cena = new BigDecimal(s.get(2));
            BigDecimal tezina = new BigDecimal(s.get(3));
            Package p = new Package(id, odr, cena, tezina, initial, magacin, isporuka);
            setDestinationCords(p, odr);
            setStartCords(p, Integer.parseInt(s.get(4)));
            ret.add(p);
        });
        
        return ret;
    }
    
    private List<Package> getPackagesMagacine(int city, boolean initial, boolean magacin, boolean isporuka){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT P.ID, P.Odrediste, ZP.CenaIsporuke, P.Tezina, P.TrenutnaLokacija"
                + " FROM [ZahtevPrevoz] ZP, [Paket] P, [Adresa] A"
                + " WHERE P.ID = ZP.Paket AND P.UVozilu = 0 AND P.UMagacinu = 1 AND P.Status = 2"
                + " AND P.TrenutnaLokacija = A.ID"
                + " AND A.Grad = ").append(String.valueOf(city))
                .append(" ORDER BY P.VremePrihvatanja ASC;");
        
        List<List<String>> packages = DB.select(sb.toString(), 5);
        
        List<Package> ret = new ArrayList<>();
        
        packages.forEach((s) -> {
            int id = Integer.parseInt(s.get(0));
            int odr = Integer.parseInt(s.get(1));
            BigDecimal cena = new BigDecimal(s.get(2));            
            BigDecimal tezina = new BigDecimal(s.get(3));
            Package p = new Package(id, odr, cena, tezina, initial, magacin, isporuka);
            setDestinationCords(p, odr);
            setStartCords(p, Integer.parseInt(s.get(4)));
            ret.add(p);
        });
        
        return ret;
    }
    
    private Vehicle getVehicleForCourier(String user){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT V.RegistracioniBroj, V.TipGoriva, V.Potrosnja, V.Nosivost"
                + " FROM [Vozilo] V, [Kurir] K"
                + " WHERE K.Vozi = V.RegistracioniBroj"
                + " AND K.KorisnickoIme = '").append(user).append("';");
        
        List<String> vehicle = DB.select(sb.toString(), 4).get(0);
        
        String id = vehicle.get(0);
        int tip = Integer.parseInt(vehicle.get(1));
        BigDecimal potrosnja = new BigDecimal(vehicle.get(2));            
        BigDecimal nosivost = new BigDecimal(vehicle.get(3));
        
        int magacine = getMagacineForUser(user);

        List<String> cord = DB.select("Adresa",
            new String[]{"X", "Y"},
            new String[]{"ID"},
            new String[]{String.valueOf(magacine)},
            null).get(0);
        
        BigDecimal x = new BigDecimal(cord.get(0));
        BigDecimal y = new BigDecimal(cord.get(1));
        
        return new Vehicle(id, tip, potrosnja, nosivost, x, y);
    }
    
    private double distance(BigDecimal x1, BigDecimal y1, BigDecimal x2, BigDecimal y2){
        BigDecimal dX = x1.subtract(x2);
        BigDecimal dY = y1.subtract(y2);
        BigDecimal mX = dX.multiply(dX);
        BigDecimal mY = dY.multiply(dY);
        BigDecimal add = mX.add(mY);
        return Math.sqrt(add.doubleValue());
    }
    
    @Override
    public boolean planingDrive(String user) {
        String regBr = getVehicle(user);
        if("-1".equals(regBr)) return false;
        if(!assignVehicle(user, regBr)) return false;
        
        int city = getCityFromUser(user);
        List<Package> cityPackages = getPackagesCity(city, true, false, true);
        List<Package> magacinePackages = getPackagesMagacine(city, false, true, true);
        
        Vehicle vehicle = getVehicleForCourier(user);
        
        ArrayList<Package> packages = new ArrayList<>();
        
        for(Package p : cityPackages){
            if(vehicle.popunjeno.add(p.tezina).compareTo(vehicle.nosivost) > 0){
                break;
            }
            vehicle.popunjeno = vehicle.popunjeno.add(p.tezina);
            packages.add(p);
        }
        
        for(Package p : magacinePackages){
            if(vehicle.popunjeno.add(p.tezina).compareTo(vehicle.nosivost) > 0){
                break;
            }
            vehicle.popunjeno = vehicle.popunjeno.add(p.tezina);
            packages.add(p);
        }
        
        if(packages.isEmpty()) return false;
        
        map.put(user, new Pair<>(vehicle, packages));
        return true;
    }

    @Override
    public int nextStop(String user) {
        Vehicle v = map.get(user).getKey();
        
        ArrayList<Package> list = new ArrayList<>();
        map.get(user).getValue().forEach((p) -> {
            if(p.initial){
                list.add(p);
            }
        });
        
        if(!list.isEmpty()){
            do {
                BigDecimal oldX = v.x, oldY = v.y;
                Package p = list.get(0);
                p.initial = false;
                pickPackage(p.id);
                p.uVozilu = true;
                list.remove(0);
                v.x = p.startX;
                v.y = p.startY;
                v.putanja = v.putanja.add(new BigDecimal(distance(v.x, v.y, oldX, oldY)));
            } while (!list.isEmpty() && v.x.equals(list.get(0).startX) && v.y.equals(list.get(0).startY));
                        
            return -2;
        }
        
        map.get(user).getValue().forEach((p) -> {
            if(p.magacin){
                list.add(p);
            }
        });

        if(!list.isEmpty()){
            BigDecimal currX = list.get(0).startX, currY = list.get(0).startY;
            v.putanja = v.putanja.add(new BigDecimal(distance(v.x, v.y, currX, currY)));
            v.x = currX;
            v.y = currY;
            
            for(Package p : list){
                pickPackage(p.id);
                p.uVozilu = true;
                p.magacin = false;
            }
            return -2;
        }
        
        map.get(user).getValue().forEach((p) -> {
            if(p.zaIsporuku && !p.isporucen){
                list.add(p);
            }
        });
        
        if(!list.isEmpty()){
            Collections.sort(list, (a, b) ->
                    distance(v.x, v.y, a.x, a.y) < distance(v.x, v.y, b.x, b.y)
                            ? -1 : distance(v.x, v.y, a.x, a.y) == distance(v.x, v.y, b.x, b.y) ? 0 : 1);
            
            
            int id = -3;
            do {
                BigDecimal oldX = v.x, oldY = v.y;
                Package p = list.get(0);
                p.initial = false;
                deliverPackage(p.id, p.odrediste, user);
                p.isporucen = true;
                p.uVozilu = false;
                v.cenaIsporuceno = v.cenaIsporuceno.add(p.cenaIsporuke);
                v.popunjeno = v.popunjeno.subtract(p.tezina);
                id = p.id;
                list.remove(0);
                v.x = p.x;
                v.y = p.y;
                v.putanja = v.putanja.add(new BigDecimal(distance(v.x, v.y, oldX, oldY)));
            } while (!list.isEmpty() && v.x.equals(list.get(0).x) && v.y.equals(list.get(0).y));
            
            int city = getCityFromCords(v.x, v.y);
            List<Package> cityPackages = getPackagesCity(city, true, false, false);
            List<Package> magacinePackages = getPackagesMagacine(city, false, true, false);
            
            for(Package p : cityPackages){
                if(v.popunjeno.add(p.tezina).compareTo(v.nosivost) > 0){
                    break;
                }
                v.popunjeno = v.popunjeno.add(p.tezina);
                map.get(user).getValue().add(p);
            }
        
            for(Package p : magacinePackages){
                if(v.popunjeno.add(p.tezina).compareTo(v.nosivost) > 0){
                    break;
                }
                v.popunjeno = v.popunjeno.add(p.tezina);
                map.get(user).getValue().add(p);
            }
                        
            return id;
        }
        
        //finished
        ArrayList<Package> list2 = new ArrayList<>();
        map.get(user).getValue().forEach((p) -> {
            if(!p.isporucen){
                list2.add(p);
            }
        });
        
        int magacine = getMagacineForUser(user);
        
        List<String> cord = DB.select("Adresa",
            new String[]{"X", "Y"},
            new String[]{"ID"},
            new String[]{String.valueOf(magacine)},
            null).get(0);
        
        BigDecimal x = new BigDecimal(cord.get(0));
        BigDecimal y = new BigDecimal(cord.get(1));
        
        
        for(Package p : list2){
            p.startX = x;
            p.startY = y;
            putInMagacine(p.id , getAddressFromCords(x, y));
            v.popunjeno = v.popunjeno.subtract(p.tezina);
        }
        
        v.putanja = v.putanja.add(new BigDecimal(distance(v.x, v.y, x, y)));
        v.x = x;
        v.y = y;
        
        calculateProfit(user, v);
        parkVehicle(magacine, v.id, user);
        
        map.remove(user);
        return -1;
    }

    private void calculateProfit(String user, Vehicle v){
        int cenaGoriva = 0;
        switch(v.tipGoriva){
            case 0:
                cenaGoriva = 15;
                break;
            case 1:
                cenaGoriva = 32;
                break;
            case 2:
                cenaGoriva = 36;
                break;
        }
        
        double profit = v.cenaIsporuceno.subtract(v.putanja.multiply(v.potrosnja).multiply(new BigDecimal(cenaGoriva))).doubleValue();
        
        String prof = DB.select("Kurir",
                new String[]{"Profit"},
                new String[]{"KorisnickoIme"},
                new String[]{user},
                null).get(0).get(0);
        
        
        DB.update("Kurir",
                new String[]{"Profit"},
                new String[]{String.valueOf(profit + Double.valueOf(prof))},
                new String[]{"KorisnickoIme"},
                new String[]{user});
        
    }
    
    private void parkVehicle(int magacine, String reg, String user){
        DB.call("spVratiVozilo", magacine, reg, user);
    }
    
    private int getMagacineForUser(String user){
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT LM.Adresa"
                + " FROM [LokacijaMagacina] LM, [Korisnik] K, [Adresa] A, [Adresa] A2"
                + " WHERE K.Adresa = A.ID AND LM.Adresa = A2.ID AND A.Grad = A2.Grad"
                + " AND K.KorisnickoIme = '").append(user).append("';");
        
        List<String> city = DB.select(sb.toString(), 1).get(0);
        return Integer.parseInt(city.get(0));
    }
    
    private void putInMagacine(int id, int adr){
        DB.update("Paket",
                new String[]{"UVozilu", "UMagacinu", "TrenutnaLokacija"},
                new String[]{"0", "1", String.valueOf(adr)},
                new String[]{"ID"},
                new String[]{String.valueOf(id)});
    }
    
    private void deliverPackage(int id, int adr, String user){
        DB.update("Paket",
                new String[]{"Status", "UVozilu", "UMagacinu", "TrenutnaLokacija"},
                new String[]{"3", "0", "0", String.valueOf(adr)},
                new String[]{"ID"},
                new String[]{String.valueOf(id)});
        
        List<String> brIsporucnih = DB.select("Kurir",
                new String[]{"BrojIsprucenihPaketa"},
                new String[]{"KorisnickoIme"},
                new String[]{user},
                null).get(0);
        
        DB.update("Kurir",
                new String[]{"BrojIsprucenihPaketa"},
                new String[]{String.valueOf(Integer.parseInt(brIsporucnih.get(0)) + 1)},
                new String[]{"KorisnickoIme"},
                new String[]{user});
    }
    
    private void pickPackage(int id){
        DB.update("Paket",
                new String[]{"Status", "UVozilu", "UMagacinu"},
                new String[]{"2", "1", "0"},
                new String[]{"ID"},
                new String[]{String.valueOf(id)});
    }
    
    @Override
    public List<Integer> getPackagesInVehicle(String user) {
        ArrayList<Integer> ret = new ArrayList<>();
        map.get(user).getValue().forEach((p) -> {
            if(p.uVozilu) ret.add(p.id);
        });
        return ret;
    }
    
}
