import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.Map;

public class first_list {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        List<Integer> nums=new ArrayList<>(Arrays.asList(1,2,2,2,2,2,2,2,3,4,1,2,1,4));

        for (int i = 0; i < 5; i++) {
        System.out.println("Enter value for index " + i);
        int val=sc.nextInt();
        nums.add(val);
        }
        nums.set(1,2345);
        for (int i = 0; i < 3; i++) {
        System.out.println(nums.get(i));
        }



        Set<Integer> st = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            System.out.println("Enter value for index " + i);
            int val = sc.nextInt();
            st.add(val);
        }
        for (Integer n : st) {
            System.out.println(n);
        }
        
        Map<Integer,Integer> mpp=new HashMap<>();

        for (int i = 0; i < nums.size(); i++) {
            mpp.merge(nums.get(i),1 , Integer::sum);
        }

        for(Map.Entry<Integer,Integer> mp:mpp.entrySet()){
            System.out.println(mp.getKey() + "-> "+mp.getValue());
        }

        mpp.forEach((key,value)->{
            System.out.println(key + "->"+ value);
        });
    }
}
