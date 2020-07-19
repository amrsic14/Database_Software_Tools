package rs.etf.sab.student;

import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

public class StudentMain {
    
    public static void main(String[] args) {
        AddressOperations addressOperations = new ma160414_AddressOperations();
        CityOperations cityOperations = new ma160414_CityOperations();
        CourierOperations courierOperations = new ma160414_CourierOperations();
        CourierRequestOperation courierRequestOperation = new ma160414_CourierRequestOperation();
        DriveOperation driveOperation = new ma160414_DriveOperation();
        GeneralOperations generalOperations = new ma160414_GeneralOperations();
        PackageOperations packageOperations = new ma160414_PackageOperations();
        StockroomOperations stockroomOperations = new ma160414_StockroomOperations();
        UserOperations userOperations = new ma160414_UserOperations();
        VehicleOperations vehicleOperations = new ma160414_VehicleOperations();
        
        TestHandler.createInstance(
                addressOperations,
                cityOperations,
                courierOperations,
                courierRequestOperation,
                driveOperation,
                generalOperations,
                packageOperations,
                stockroomOperations,
                userOperations,
                vehicleOperations);

        TestRunner.runTests();
    }
    
}
