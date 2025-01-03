package controllers;

import models.Source;
import models.stock.Group;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stock.groups.edit;

import java.util.List;

public class GroupController extends Controller
{
    public static Result index()
    {
        return ok();
    }

    public static Result all()
    {
        List<Group> groups = Group.finder.all();
        return ok(Json.toJson(groups));
    }

    public static Result find(Long id)
    {
        Group group = Group.find(id);
        if (group == null)
        {
            return notFound();
        }
        return ok(edit.render(group));
    }

    public static Result updateSource(Long id)
    {
        Source source = Source.find(id);
        if (source == null)
        {
            return notFound();
        }
        return ok(views.html.stock.updateSource.render(source));
    }

    public static Result delete(Long id)
    {
        Group group = Group.find(id);
        if (group == null)
        {
            return notFound();
        }
        group.deleteStatus = true;
        group.update();
        return ok();
    }

    public static Result deleteSource(Long id)
    {
        Source source = Source.find(id);
        if (source == null)
        {
            return notFound();
        }
        source.deleteStatus = true;
        source.update();
        return ok();
    }


    public static Result save()
    {
        Form<Group> groupForm=Form.form(Group.class).bindFromRequest();
        Group group=new Group();
        group.name=groupForm.field("name").value();
        group.acronym=groupForm.field("acronym").value();
        group.description=groupForm.field("description").value();
        group.save();
        return ok("1");
    }
    public static Result saveSource()
    {
        Form<Source> groupForm=Form.form(Source.class).bindFromRequest();
        Source source = new Source();
        source.name=groupForm.field("name").value();
        source.description=groupForm.field("description").value();
        source.save();
        return ok("1");
    }

    public static Result update()
    {
        Form<Group> groupForm=Form.form(Group.class).bindFromRequest();
        Group group = Group.find(Long.parseLong(groupForm.field("id").value()));
        if (group == null)
        {
            return notFound();
        }
        group.name=groupForm.field("name").value();
        group.acronym=groupForm.field("acronym").value();
        group.description=groupForm.field("description").value();
        group.update();
        return ok("1");
    }

    public static Result updateSourceFund()
    {
        Form<Source> form = Form.form(Source.class).bindFromRequest();
        Source source = Source.find(Long.parseLong(form.field("id").value()));
        if (source == null)
        {
            return notFound();
        }
        source.name=form.field("name").value();
        source.description=form.field("description").value();
        source.update();
        return ok("1");
    }
}
