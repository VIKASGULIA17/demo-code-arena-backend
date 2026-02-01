import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class exercise_1 {
    public static void main(String[] args) {
        Map<String,Integer> mpp=new HashMap<>();

        List<Integer> Marks=new ArrayList<>(Arrays.asList(89,56,87,97,45,97));
        List<String> Name=new ArrayList<>(Arrays.asList("Rohan","luffy","Shino","Vikas","Gahlot","Shinomiya"));


        for(int i=0;i<6;i++){
            int marks=Marks.get(i);
            String name=Name.get(i);

            mpp.put(name,marks);
        }

        mpp.forEach((key,val)->{
            if(val>88){
                System.out.println(key +" has scored : " +val );
            }
        });
    }
}
