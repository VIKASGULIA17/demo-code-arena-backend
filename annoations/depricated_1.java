public class depricated_1 {
    @SuppressWarnings("deprecation")
    public static void main(String[] args) {
        Calculator c=new Calculator();
        c.addOld(10, 20);
        c.add(10, 20);
    }
}


class Calculator{
    @Deprecated
    public void addOld(int a,int b){
        System.out.println(a+b);
    }
    public void add(int a,int b){
        System.out.println(a+b);
    }
};