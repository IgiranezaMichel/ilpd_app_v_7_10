package controllers;
import models.stock.Category;
import models.stock.Item;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stock.items.edit;
import views.html.stock.items.editAsset;

import java.text.SimpleDateFormat;

public class ItemsController extends Controller
{
    public static Result edit(Long id)
    {
        Item item = Item.find(id);
        if (item == null)
        {
            return notFound();
        }
        return ok(edit.render(item, Category.all()));
    }
    public static Result AddHarmonization(Long id)
    {
        Item item = Item.find(id);
        if (item == null)
        {
            return notFound();
        }
        return ok(views.html.stock.AddHarmonization.render(item));
    }
    public static Result removeHarmonization(Long id)
    {
        Item item = Item.find(id);
        if (item == null)
        {
            return notFound();
        }
        return ok(views.html.stock.removeHarmonization.render(item));
    }
    public static Result deleteHarmonization(Long id)
    {
        Item item = Item.find(id);
        if (item == null)
        {
            return notFound();
        }
        return ok(views.html.stock.deleteHarmonization.render(item));
    }
    public static Result editAsset(Long id)
    {
        Item item = Item.find(id);
        if (item == null)
        {
            return notFound();
        }
        return ok(editAsset.render(item, Category.all()));
    }

    public static Result delete(Long id)
    {
        Item item = Item.find(id);
        if (item == null)
        {
            return notFound();
        }
        item.deleteStatus = true;
        item.update();
        return ok();
    }


    public static Result save()
    {
        Form<Item> itemForm=Form.form(Item.class).bindFromRequest();
        Category  category=Category.find(Long.parseLong(itemForm.field("category").value()));
        if(category==null){
            return notFound();
        }
        // Logger.debug("Form data:"+itemForm.toString());
        Item item=new Item();
        item.category=category;
        item.name=itemForm.field("name").value();
        item.unitMeasure=itemForm.field("unitMeasure").value();
        item.min=Integer.parseInt(itemForm.field("min").value());
        item.max=Integer.parseInt(itemForm.field("max").value());
        item.beginingQty=Integer.parseInt(itemForm.field("beginingQty").value());
        item.purchaseQty=0;
        item.consumptionQty=0;
        item.balanceQty=item.beginingQty;
        item.iType=itemForm.field("type").value();

        try{
            item.depleciationMinMonths=Integer.parseInt(itemForm.field("depleciationMinMonths").value());
        }catch (NumberFormatException e){
            System.out.print(e.getMessage());
        }
        if(Item.checkExist(itemForm.field("name").value())) return ok("Same item name "+item.name+" already exist!");
        item.save();
        return ok("1");
    }

    public static Result update(Long id)
    {
        Form<Item> itemForm=Form.form(Item.class).bindFromRequest();
        Item item=Item.find(id);
        if(item==null){
            return notFound();
        }
        Category  category=Category.find(Long.parseLong(itemForm.field("category").value()));
        if(category==null){
            return notFound();
        }
        item.category=category;
        int qty = Integer.parseInt(itemForm.field("balanceQty").value());
        item.name=itemForm.field("name").value();
        item.unitMeasure=itemForm.field("unitMeasure").value();
        item.unitPrice = Double.parseDouble(itemForm.field("unitPrice").value());
        item.iType=itemForm.field("type").value();
        item.min=Integer.parseInt(itemForm.field("min").value());
        item.max=Integer.parseInt(itemForm.field("max").value());
        int beginning = 0;
        if(qty > item.balanceQty){
            beginning = item.balanceQty + item.beginingQty - item.balanceQty;
        }else{
            beginning = item.balanceQty + item.beginingQty + item.balanceQty;
        }
        item.beginingQty = beginning;
        item.balanceQty = beginning;
        item.depleciationMinMonths=Integer.parseInt(itemForm.field("depleciationMinMonths").value());
        item.update();
        return ok("1");
    }
}
