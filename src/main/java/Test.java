public class Test {
    public static void main(String[] args) {
//        String text = "(\\-,)*(\\:)*(VBPZ)*(;BPZ)*( s)*([/()]";
//        System.out.println(ProductNameResolver.resolve(text));
//
//        String text2 = "aasd";
//        String text3 = "4dfdf";
//        System.out.println(text2.matches("^\\d.*"));
//        System.out.println(text3.matches("^\\d.*"));
//        System.out.println(Arrays.toString(text.split("[,;]")));
//
//        //только цифры
//        String numbers = "410355768";
//        System.out.println(numbers.matches("^\\d+$"));

        //содержит символы - и /
       /* String[] spec = {
                "G120P-11/32B",
                "AP 221/10",
                "PBA-1191-ULC",
                "DF1101-EX(ULC)",
                "COIB0801",
                "6FL7800-8DH02",
                "7471800210",
                "OP720",
                "ROLP/R/S",
                "ROLP-LX-RW",
                "ROLP-W",
                "ACTUATOR",
                "5WG11838AA11",
                "F14A410",
                "ACC-AP",
                "ACC-APM-2420",
                "AC5200",
                "S55560-F117",
                "S55384-C886-F100",
                "S588/12",
                "SV24V-150W-A5",
                "ACXxx.xxx",
                "FCAxxx-A1",
                "CMD.xx",
                "M3C700"
        };
//        Pattern pattern = Pattern.compile("^(\\d*?[A-Z]*?\\d*?[A-Z]+)(.*[-/]*.*)$");
//        Pattern pattern = Pattern.compile("^([A-Z0-9]*[A-Z]+)(\\d*.*[-/]*.*)$");
//        Pattern pattern = Pattern.compile("^(\\d*[A-Z]+)([0-9-/]*.*)$");
//        Pattern pattern = Pattern.compile("^(\\d+[A-Z]+)?([A-Z]+\\d*[A-Z]+)?([0-9-/]*.*)$");
        Pattern pattern = Pattern.compile("^(\\d*?[A-Z]*?\\d*[A-Z]+)?(\\d+)?([0-9-/]*.*)$");
        for (String s : spec) {
            System.out.println(s + ": ");
            Matcher matcher = pattern.matcher(s);
            if (matcher.matches()) {
                for (int i = 0; i < matcher.groupCount(); i++) {
                    System.out.print("\t" + i + ". " + matcher.group(i) + "\n");
                }
                System.out.println();
            } else {
                System.out.println("не совпало");
            }
        }

        System.out.println("----------------------------------");

        for (String s : spec) {
            System.out.println(s + " -> " + s.replaceAll("\\s*(x+.*$)*", ""));
        }*/


        /*System.out.println("=====================================");
        String[] lgbks = new String[]{
                "BA1DQ",
                "1SIPA",
                "1SIRZ",
                "BM3DA",
                "1SMKA"
        };
//        Pattern pattern1 = Pattern.compile("^\\d?([A-Z0-9]{3}).*$");
        Pattern pattern1 = Pattern.compile("(\\d)?([A-Z0-9]{3})(.*)?");
        for (String l : lgbks) {
            Matcher matcher = pattern1.matcher(l);
            if (matcher.matches()) {
                for (int i = 0; i < matcher.groupCount(); i++) {
                    System.out.print("\t" + i + ". " + matcher.group(i) + "\n");
                }
                System.out.println();
            } else {
                System.out.println("не совпало");
            }

        }

        String name = "price_comparison_report_PL_FY21_2020.10.16.xlsx vs PL_FY21_2020.10.01.xlsx";
        name = name.replaceAll(".xlsx", "");
        System.out.println(name);*/
    }


}
