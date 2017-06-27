package ru.aygur.testdbxml;

public class Main {

    public static void main(String[] args) {
        //init
        int n = 0;
        if(args.length > 0){
            try {
                n = Integer.parseInt(args[0]);
                System.out.println("Argument: " + n);
            } catch (NumberFormatException ex) {
                System.out.println("Argument isn't number");
                System.exit(0);
            }
        } else {
            System.out.println("Please, add N in argument");
            System.exit(0);
        }

        long timeStart = System.currentTimeMillis();
        MinDAO minDAO = new MinDAO();
        minDAO.setConnector(new Connector("magnit_test", "root", "mysql2017"));
        minDAO.setN(n);

        try {
            minDAO.insertNToDB();
            minDAO.generateXMLFile();
            minDAO.transformXMLFile();
            System.out.println("Arithmetic sum of the attributes field equal to " + minDAO.sumFieldFromXML());
        } catch (CriticalException e) {
            System.out.println(e.getMessage());
            System.out.println(e.getCause());
            System.exit(1);
        }

        System.out.println("Finish. Average time: " + (System.currentTimeMillis() - timeStart) + " ms");

    }
}
