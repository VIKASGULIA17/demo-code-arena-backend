import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class exercise_2 {
    public static void main(String[] args) {
        List<Integer> nums=new ArrayList<>(Arrays.asList(121,44,567,234,232));


        List<Integer> temp=nums.stream()
        .filter(val-> isPalindrome(val)).collect(Collectors.toList());
        
        // for(Integer num:temp){
        //     System.out.println(num +" is a palindrome");
        // }

        for(int i=0;i<temp.size();i++){
            System.out.println(temp.get(i));
        }
        
    }
        public static boolean isPalindrome(int val){
        int temp=val;
        int reverse=0;

        while(temp>0){
            int rem=temp%10;
            reverse=(reverse*10)+rem;
            temp/=10;
        }

        return reverse==val;
    }
}

