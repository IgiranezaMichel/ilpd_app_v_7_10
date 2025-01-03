package controllers;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Expression;
import com.fasterxml.jackson.databind.JsonNode;
import models.Utility.Contracts;
import models.stock.Category;
import models.stock.Item;
import models.stock.StockMovement;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;

public class DynamicReportController extends Controller {

    public static Result DynamicReport(String t){
        if (t.equalsIgnoreCase("assetsReport")) {

            Form<Item> form = Form.form(Item.class).bindFromRequest();
            String startDate = form.field("startDate").valueOr("3000");
            String endDate = form.field("endDate").valueOr("0");

            Contracts<Item> itemContracts = new Contracts<>(Item.class);
            JsonNode jsonNode;
            if (startDate.equalsIgnoreCase("") || endDate.equalsIgnoreCase("")) {
                jsonNode = itemContracts
                        .setPageExp(Expr.eq("iType", "Non-consumable"))
                        ._structNodeList();
            }
            else {
                jsonNode = itemContracts
                        .setPageExp(Expr.and(Expr.between("date", startDate, endDate), Expr.eq("iType", "Non-consumable")))
                        ._structNodeList();
            }

            return ok(jsonNode);
        }
        else if( t.equalsIgnoreCase("byCat") ){

            Form<Item> form = Form.form(Item.class).bindFromRequest();

            Long catId = Long.parseLong(form.field("categoryId").value());
            Category category = Category.find(catId);

            if( category == null ) return  ok();

            String startDate = form.field("startDate").valueOr("3000");
            String endDate = form.field("endDate").valueOr("0");

            Contracts<Item> itemContracts = new Contracts<>(Item.class);
            JsonNode jsonNode;
            if (startDate.equalsIgnoreCase("") || endDate.equalsIgnoreCase("")) {
                jsonNode = itemContracts
                        .setPageExp(Expr.eq("iType", "Non-consumable"))
                        ._structNodeList();
            }
            else {
                Expression eq = Expr.eq("category.id", category.id);
                Expression expression = Expr.and(Expr.between("date", startDate, endDate), Expr.and(Expr.eq("iType", "Non-consumable"), eq));
                jsonNode = itemContracts
                        .setPageExp(expression)
                        ._structNodeList();
            }

            return ok(jsonNode);
        }
        if( t.equalsIgnoreCase("byType") ){
            Form<Item> form = Form.form(Item.class).bindFromRequest();
            String itemType = form.field("iType").value();
            if( itemType == null ) return  ok();
            String startDate = form.field("startDate").valueOr("3000");
            String endDate = form.field("endDate").valueOr("0");
            Contracts<Item> itemContracts = new Contracts<>(Item.class);
            JsonNode jsonNode;
            if (startDate.equalsIgnoreCase("") || endDate.equalsIgnoreCase("")) {
                jsonNode = itemContracts
                        ._structNodeList();
            }
            else {
                Expression eq = Expr.eq("iType", itemType);
                Expression expression = Expr.and(Expr.between("date", startDate, endDate),eq);
                jsonNode = itemContracts
                        .setPageExp(expression)
                        ._structNodeList();
            }

            return ok(jsonNode);
        }
        if( t.equalsIgnoreCase("byMvt") ){
            Form<StockMovement> form = Form.form(StockMovement.class).bindFromRequest();
            String startDate = form.field("startDate").valueOr("3000");
            String endDate = form.field("endDate").valueOr("0");
            Contracts<StockMovement> itemContracts = new Contracts<>(StockMovement.class);
            JsonNode jsonNode;
            if (startDate.equalsIgnoreCase("") || endDate.equalsIgnoreCase("")) {
                jsonNode = itemContracts
                        ._structNodeList();
            }
            else {
                Expression expression = Expr.between("date", startDate, endDate);
                jsonNode = itemContracts
                        .setPageExp(expression)
                        ._structNodeList();
            }

            return ok(jsonNode);
        }
        return ok();
    }
}
