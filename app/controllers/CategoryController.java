package controllers;

import models.stock.Category;
import models.stock.Group;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;


import java.util.List;

public class CategoryController extends Controller
{
    public static Result index()
    {

        return ok();
    }

    public static Result all()
    {
        List<Category> categories  = Category.finder.all();
        return ok(Json.toJson(categories));
    }

    public static Result find(Long id)
    {
        Category category = Category.find(id);
        if (category == null)
        {
            return notFound();
        }
        return ok(views.html.stock.categories.edit.render(category,Group.all()));
    }

    public static Result delete(Long id)
    {
        Category category = Category.find(id);
        if (category == null)
        {
            return notFound();
        }
        category.deleteStatus = true;
        category.update();
        return ok();
    }


    public static Result save()
    {
        Form<Category> categoryForm=Form.form(Category.class).bindFromRequest();
        Category category=new Category();
        Group group = Group.find(Long.parseLong(categoryForm.field("group").value()));
        if (group == null)
        {
            return notFound();
        }
        category.group=group;
        category.name=categoryForm.field("name").value();
        category.acronym=categoryForm.field("acronym").value();
        category.description=categoryForm.field("description").value();
        category.save();
        return ok("1");
    }

    public static Result update()
    {
        Form<Category> categoryForm=Form.form(Category.class).bindFromRequest();
        Category category = Category.find(Long.parseLong(categoryForm.field("id").value()));
        if (category == null)
        {
            return notFound();
        }
        Group group = Group.find(Long.parseLong(categoryForm.field("group").value()));
        if (group == null)
        {
            return notFound();
        }
        category.group=group;
        category.name=categoryForm.field("name").value();
        category.acronym=categoryForm.field("acronym").value();
        category.description=categoryForm.field("description").value();
        category.update();
        return ok("1");
    }

}
