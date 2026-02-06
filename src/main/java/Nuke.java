public class Nuke {
    public static void main(String[] args) {
        start("C:\\Users\\joshu\\OneDrive\\Documents\\GitHub\\ip\\config.txt");
        Action.chat();
        exit();
    }
    protected static void start(String config) {
        try {
            Action.start(config);
            System.out.println(Bank.LOGO_LOBOTOMY);
            System.out.println(Bank.GREETING);
            System.out.println(Bank.LINE);
        } catch (Exception e){
            return;
        }
    }
    protected static void exit() {
        System.out.println(Bank.LOGO_AME);
        System.out.println(Bank.FAREWELL);
    }
}
