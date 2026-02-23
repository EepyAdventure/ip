public class Nuke {
    private static Process process;
    public static void main(String[] args) {
        start("C:\\Users\\joshu\\OneDrive\\Documents\\GitHub\\ip\\config.txt");
        process.chat();
        exit();
    }
    protected static void start(String config) {
        try {
            process = Process.init(config);
            System.out.println(Bank.LOGO_LOBOTOMY);
            System.out.println(Bank.GREETING);
            System.out.println(Bank.LINE);
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    protected static void exit() {
        System.out.println(Bank.LOGO_AME);
        System.out.println(Bank.FAREWELL);
    }
}
