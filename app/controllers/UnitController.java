package controllers;

import models.stock.Block;
import models.stock.Location;
import models.stock.Unit;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stock.units.edit;
public class UnitController extends Controller
{

    public static Result edit(Long id)
    {
        Unit unit = Unit.find(id);
        if (unit == null)
        {
            return notFound();
        }
        return ok(edit.render(unit));
    }

    public static Result delete(Long id)
    {
        Unit unit = Unit.find(id);
        if (unit == null)
        {
            return notFound();
        }
        unit.deleteStatus = true;
        unit.update();
        return ok();
    }


    public static Result save()
    {
        Form<Unit> unitForm=Form.form(Unit.class).bindFromRequest();
        Unit unit=new Unit();
        unit.name=unitForm.field("name").value();
        unit.acronym=unitForm.field("acronym").value();
        unit.description=unitForm.field("description").value();
        unit.save();
        return ok("1");
    }

    public static Result update(Long id)
    {
        Form<Unit> unitForm=Form.form(Unit.class).bindFromRequest();
        Unit unit = Unit.find(id);
        if (unit == null)
        {
            return notFound();
        }
        unit.name=unitForm.field("name").value();
        unit.acronym=unitForm.field("acronym").value();
        unit.description=unitForm.field("description").value();
        unit.update();
        return ok("1");
    }


/*
    Block
 */

public static Result saveOrdUpdateBlock(){
    Form<Block>  blockForm=Form.form(Block.class).bindFromRequest();
    Block block =new Block();
    block.name=blockForm.field("name").value();
    block.acronym=blockForm.field("acronym").value();

    if(blockForm.field("id").value()!=null){
        Block b=Block.find(Long.parseLong(blockForm.field("id").value()));
        if(b==null){
            return notFound();
        }
        block.update(b.id);
    }else{
        block.save();
    }
    return ok("1");
}



/*
    Location
 */

public static Result saveOrdUpdateLocation(){
    Form<Location>  locationForm=Form.form(Location.class).bindFromRequest();
    Location location =new Location();
    location.name=locationForm.field("name").value();
    location.acronym=locationForm.field("acronym").value();
    location.block=Block.find(Long.parseLong(locationForm.field("block").value()));

    if(locationForm.field("id").value()!=null){
        Location b=Location.find(Long.parseLong(locationForm.field("id").value()));
        if(b==null){
            return notFound();
        }
        location.update(b.id);
    }else{
        location.save();
    }
    return ok("1");
}




}
