package controllers;

import models.stock.Position;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stock.positions.edit;

public class PositionController extends Controller
{
    public static Result edit(Long id)
    {
        Position position = Position.find(id);
        if (position == null)
        {
            return notFound();
        }
        return ok(edit.render(position));
    }
    public static Result delete(Long id)
    {
        Position position = Position.find(id);
        if (position == null)
        {
            return notFound();
        }
        position.deleteStatus = true;
        position.update();
        return ok();
    }


    public static Result save()
    {
        Form<Position> positionForm=Form.form(Position.class).bindFromRequest();
        Position position=new Position();
        position.name=positionForm.field("name").value();
        position.acronym=positionForm.field("acronym").value();
        position.description=positionForm.field("description").value();
        position.save();
        return ok("1");
    }

    public static Result update(Long id)
    {
        Form<Position> positionForm=Form.form(Position.class).bindFromRequest();
        Position position = Position.find(id);
        if (position == null)
        {
            return notFound();
        }
        position.name=positionForm.field("name").value();
        position.acronym=positionForm.field("acronym").value();
        position.description=positionForm.field("description").value();
        position.update();
        return ok("1");
    }
}
