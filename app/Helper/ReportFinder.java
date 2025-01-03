package Helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReportFinder {
    private List<Class<?>> classList = new ArrayList<>();
    private List<JsonNode> nodeList = new ArrayList<>();
    private String saveRoute = "/";


    public ReportFinder putMenu(String title,List<ReportMenu> menuList){
        ObjectNode objectNode = Json.newObject();
        objectNode.put("title",title);
        objectNode.put("content",this.process(menuList,title));
        nodeList.add(objectNode);
        return this;
    }

    public JsonNode putMenuFinal(String title,List<ReportMenu> menuList){
        ObjectNode objectNode = Json.newObject();
        objectNode.put("title",title);
        objectNode.put("content",this.process(menuList,title));
        return objectNode;
    }

    private JsonNode process(List<ReportMenu> menuList,String title){
        ObjectNode node = Json.newObject();
        for (ReportMenu menu : menuList){
            ObjectNode objectNode = Json.newObject();
            menu.putNode(objectNode,node);
        }
        ObjectNode form = Json.newObject();
        form.put("formData",node);
        form.put("formHead",Json.newObject()
                .put("formName",title)
                .put("buttonName","View report")
                .put("saveRoute",saveRoute));
        return form;
    }

    public ReportFinder putRoute(String saveRoute){
        this.saveRoute = saveRoute;
        return this;
    }

    public ReportFinder putMenu(String title,ReportMenu ... menus){
        this.putMenu(title, Arrays.asList(menus));
        return this;
    }

    public JsonNode getMenuJson(){
        return getMenuJson("Report controller");
    }

    public JsonNode getMenuJson(String title){
        ObjectNode node = Json.newObject();
        node.put("nodeList",Json.toJson(nodeList));
        node.put("title",title);
        return node;
    }
}
