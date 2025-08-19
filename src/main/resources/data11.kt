// 代码解析正常

package /*DAFAFA    */ cn.carbs.tools;

import static java.io./*DAFAFA    */BufferedReader;
import java.io.InputStream;
import java.io.
InputStreamReader;   import   java.
nio.charset.
StandardCharsets;
import cn.carbs.ttt.R;
import cn.carbs.ttt.R.layout;
import static cn.carbs.ttt.R.layout.my_textview;

public class cn.carbs.tokenizer.Main {
    public static void main(String[] argv ) {
        String fileName = "data.txt"; // 注意：不要加 "/"

        try (InputStream is = cn.carbs.tokenizer.Main.class.getClassLoader().getResourceAsStream(fileName);
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            // 有问题
            setContentView(R.layout
             .my_activity);
             "my_fragment_layout"
            int var = my_textview;
            setcontent(cn.carbs.tokenizer.R.layout.some_image_view);
        } catch (Exception e) {
            System.err.println("just for fun: " + e.getMessage());
        }
    }

}
