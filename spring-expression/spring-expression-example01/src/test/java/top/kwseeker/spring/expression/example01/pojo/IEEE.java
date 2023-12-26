package top.kwseeker.spring.expression.example01.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IEEE {

    static Inventor tesla ;
    static Inventor pupin ;

    static {
        tesla = new Inventor("Nikola Tesla", "Serbian");
        pupin = new Inventor("Pupin", "Idvor");
    }

    private String name;

    public List<Object> Members = new ArrayList<>();
    public List<Object> Members2 = new ArrayList<>();
    public Map<String,Object> officers = new HashMap<>();

    public List<Map<String, Object>> reverse = new ArrayList<>();

    public IEEE() {
        officers.put("president", pupin);
        List<Object> advisors = new ArrayList<>();
        advisors.add(tesla);
        officers.put("advisors", advisors);
        Members2.add(tesla);
        Members2.add(pupin);

        reverse.add(officers);
    }

    public boolean isMember(String name) {
        return true;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) { this.name = n; }
}