package controllers;

import models.Employee;
import models.stock.Supplier;
import models.stock.Position;
import models.stock.Unit;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stock.suppliers.edit;

public class EmployeesController extends Controller
{
    public static Result saveEmployee()
    {
        Form<Employee> form = Form.form(Employee.class).bindFromRequest();
        Employee employee = new Employee();
        Unit unit = Unit.find(Long.parseLong(form.field("unit").value()));
        if (unit == null)
        {
            return notFound();
        }
        employee.employeeFirstName = form.field("name").value();
        employee.employeeLastName = form.field("email").value();

        employee.position = Position.find(Long.valueOf(form.field("position").value()));

        employee.unit = unit;

        if (form.field("isHeadOfUnit").value() != null)
        {
            Employee headOfUnit = Employee.finder.where().eq("deleteStatus", false).eq("isHeadOfUnit", true).eq("unit.id", unit.id).setMaxRows(1).findUnique();
            if (headOfUnit == null)
            {
                employee.isHeadOfUnit = true;
            } else
            {
                headOfUnit.isHeadOfUnit = false;
                headOfUnit.update();
                employee.isHeadOfUnit = true;
            }
        }
        employee.save();
        return ok("1");
    }




    public static Result saveSupplier()
    {
        Form<Supplier> form = Form.form(Supplier.class).bindFromRequest();
        Supplier supplier = new Supplier();
        Supplier gotSupplier = form.get();
        supplier.name = gotSupplier.name;
        supplier.tinNumber = gotSupplier.tinNumber;
        supplier.address = gotSupplier.address;
        supplier.email = gotSupplier.email;
        supplier.phone = gotSupplier.phone;
        supplier.save();
        return ok("1");
    }



    public static Result updateSupplier(Long id)
    {
        Form<Supplier> form = Form.form(Supplier.class).bindFromRequest();
        Supplier supplier = Supplier.finder.byId(id);
        Supplier gotSupplier = form.get();
        supplier.name = gotSupplier.name;
        supplier.tinNumber = gotSupplier.tinNumber;
        supplier.address = gotSupplier.address;
        supplier.email = gotSupplier.email;
        supplier.phone = gotSupplier.phone;
        supplier.update();
        return ok("1");
    }



    public static Result editSupplier(Long id)
    {
        Supplier supplier = Supplier.finder.byId(id);
        if(supplier==null){
            return notFound();
        }
        return ok(edit.render(supplier));
    }


    public static Result deleteSupplier(Long id)
    {
        Supplier supplier = Supplier.finder.byId(id);
        if(supplier==null){
            return notFound();
        }
        supplier.deleteStatus=true;
        supplier.update();
        return ok("1");
    }
}
