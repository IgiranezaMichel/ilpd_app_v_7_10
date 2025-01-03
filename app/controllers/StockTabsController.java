package controllers;
import com.avaje.ebean.Ebean;
import com.avaje.ebean.Transaction;
import models.*;
import models.stock.Supplier;
import models.stock.*;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.stock.groups.index;
import views.html.stock.pages.itemsPage;
import java.util.Date;
import java.util.List;

import play.data.Form;
import views.html.stock.requests.myRequest;
import views.html.student.certificate;

import static controllers.Application.Ins;
import static controllers.LectureController.activeL;
import static controllers.React.defaultSession;

public class StockTabsController extends Controller {

    public static Result meritCertificate() {
        if (session("DTR/Coordinator") != null || session("Registrar") != null) {
        return ok(views.html.reports.meritCertificate.render());
        }
        return ok("0");
    }
    public static Result ItemPage() {
        return ok(itemsPage.render());
    }
    public static Result harmonizeStock() {
        return ok(views.html.stock.harmonizeStock.render());
    }
    public static Result certificateMerit() {
        Form<Student> f = Form.form(Student.class).bindFromRequest();
        Student student = f.get();
        Student student1 = Student.findByRegNo(f.field("regno").value());
        return ok(certificate.render(student1));
    }
    public static Result ItemsPage(String tab) {
        if (tab.equalsIgnoreCase("sources")) {
            List<Source> sources = Source.all();
            return ok(views.html.stock.sources.render(sources));
        }
        if (tab.equalsIgnoreCase("groups")) {
            List<Group> groups = Group.all();
            return ok(index.render(groups));
        } else if (tab.equalsIgnoreCase("assignmentReport")) {
            List<Assignment> assignments = Assignment.finder.where()
                    .eq("deleteStatus", false)
                    .eq("grouped", false)
                    .findList();
            List<Training> trainings = Training.allOpen();
            return ok(views.html.lecture.assignmentReport.render(assignments, trainings));
        } else if (tab.equalsIgnoreCase("assignmentReportClaim")) {
            List<Assignment> assignments = Assignment.finder.where()
                    .eq("deleteStatus", false)
                    .findList();
            List<Training> trainings = Training.allOpen();
            return ok(views.html.lecture.assignmentResults.render(assignments, trainings));
        } else if (tab.equalsIgnoreCase("assignmentReportSubmitted")) {
            DynamicForm form = DynamicForm.form().bindFromRequest();
            Assignment assignment = Assignment.finderById(Long.parseLong(form.field("assignment").value()));
            if (assignment == null) {
                return notFound();
            }
            List<Submission> students = Submission.finder.where()
                            .eq("assignment.id", assignment.id)
                            .eq("status", "pending")
                    .findList();
            return ok(views.html.lecture.studentNotSubmittedAss.render(students, assignment));
        }else if (tab.equalsIgnoreCase("assignmentReportSubmittedCliamR")) {
            DynamicForm form = DynamicForm.form().bindFromRequest();
            Assignment assignment = Assignment.finder.byId(Long.parseLong(form.field("assignment").value()));
            if (assignment == null) {
                return notFound();
            }
            List<AssignmentResult> students = AssignmentResult.find.where()
                            .eq("assignment.id", assignment.id)
                    .findList();
            return ok(views.html.lecture.studentNotSubmittedAssClaim.render(students, assignment));
        }else if (tab.equalsIgnoreCase("assignmentMarksReport")) {
            DynamicForm form = DynamicForm.form().bindFromRequest();
            Assignment assignment = Assignment.finder.byId(Long.parseLong(form.field("assignment").value()));
            List<AssignmentResult> assignmentResult = AssignmentResult.find
                    .where()
                    .eq("assignment.id", assignment.id)
                    .findList();
            if (assignment == null) {
                return notFound();
            }
            return ok(views.html.lecture.assignmentResult.render(assignmentResult, assignment));
        } else if (tab.equalsIgnoreCase("assignmentReportSubmittedYes")) {
            DynamicForm form = DynamicForm.form().bindFromRequest();
            Assignment assignment = Assignment.finderById(Long.parseLong(form.field("assignment").value()));
            if (assignment == null) {
                return notFound();
            }
            List<Submission> students = Submission.finder.where()
                            .eq("assignment.id", assignment.id)
                    .findList();
            return ok(views.html.lecture.studentNotSubmittedAssYes.render(students, assignment));
        } else if (tab.equalsIgnoreCase("courseMaterials")) {
            Lecture lecture = activeL();
            if( lecture == null ) return ok();
            List<CourseMaterial> courseMaterials = CourseMaterial.materialList(lecture.id);
            return ok(views.html.lecture.courseMaterial.render(courseMaterials, lecture));
        } else if (tab.equalsIgnoreCase("categories")) {
            List<Category> categories = Category.all();
            List<Group> groups = Group.all();
            return ok(views.html.stock.categories.index.render(categories, groups));
        } else if (tab.equalsIgnoreCase("items")) {
            List<Item> items = Item.finder.where()
                    .eq("iType", "Consumable")
                    .eq("deleteStatus", false)
                    .findList();
            return ok(views.html.stock.items.index.render(items, Category.all()));
        }else if (tab.equalsIgnoreCase("stock-harmo")) {
            List<Item> items = Item.finder.where()
                    .eq("deleteStatus", false)
                    .orderBy("name asc")
                    .findList();
            return ok(views.html.stock.stockHarmo.render(items, Category.all()));
        } else if (tab.equalsIgnoreCase("assets")) {
            List<Item> items = Item.finder.where()
                    .eq("iType", "Non-consumable")
                    .eq("deleteStatus", false)
                    .findList();
            return ok(views.html.stock.items.assets.render(items, Category.all()));
        } else if (tab.equalsIgnoreCase("reports")) {
            return ok(views.html.stock.pages.reportPage.render());
        }  else if (tab.equalsIgnoreCase("harmonizeCourses")) {
            return ok(views.html.admininistrator.harmonizeCourses.render());
        }  else if (tab.equalsIgnoreCase("harmonizeApplicants")) {
            return ok(views.html.admininistrator.harmonizeApplicants.render());
        }  else if (tab.equalsIgnoreCase("deleteUncompleted")) {
            return ok(views.html.admininistrator.deleteUncompleted.render());
        }  else if (tab.equalsIgnoreCase("hashPassword")) {
            return ok(views.html.admininistrator.hashPassword.render());
        } else if (tab.equalsIgnoreCase("reseatPage")) {
            Student student = Ins("student").me();
            List<Module> modules = student.myOnlyModulesDeliberation();
            return ok(views.html.student.reseats.render(modules, student));
        } else if (tab.equalsIgnoreCase("certificate")) {
            Student student = Ins("student").me();
            List<Module> moduleList = student.getReSeat();
            return ok(views.html.student.certificate.render(student));
        } else if (tab.equalsIgnoreCase("dynamicReport")) {
            return ok(views.html.stock.reports.dynamicReport.render());
        }
        return ok("Page not found");
    }


    public static Result StaffTabs() {
        return ok(views.html.stock.pages.staffTabs.render());
    }

    public static Result StaffsPage(String tab) {
        if (tab.equalsIgnoreCase("units")) {
            List<Unit> list = Unit.all();
            return ok(views.html.stock.units.index.render(list));
        } else if (tab.equalsIgnoreCase("positions")) {
            List<Position> list = Position.all();
            return ok(views.html.stock.positions.index.render(list));
        } else if (tab.equalsIgnoreCase("employees")) {
            List<Employee> list = Employee.finder.all();
            return ok(views.html.stock.employees.index.render(list));
        } else if (tab.equalsIgnoreCase("suppliers")) {
            List<Supplier> list = Supplier.finder.where().eq("deleteStatus", false).findList();
            return ok(views.html.stock.suppliers.index.render(list));
        } else if (tab.equalsIgnoreCase("block")) {
            List<Block> list = Block.all();
            return ok(views.html.stock.block.index.render(list));
        } else if (tab.equalsIgnoreCase("location")) {
            List<Location> list = Location.all();
            return ok(views.html.stock.location.index.render(list));
        } else if (tab.equalsIgnoreCase("itemsReport")) {
            return ok(views.html.stock.reports.items.render());
        } else if (tab.equalsIgnoreCase("updateCourse")) {
            List<CourseMaterial> courseMaterials = CourseMaterial.all();
            return ok(views.html.admininistrator.updateCourse.render(courseMaterials));
        } else if (tab.equalsIgnoreCase("updateCourse-app")) {
            List<Applied> applicantList = Applied.allDup();
            return ok(views.html.admininistrator.updateApp.render(applicantList));
        } else if (tab.equalsIgnoreCase("deleteApplicant-app")) {
            List<Applied> applicantList = Applied.allUncompleted();
            return ok(views.html.admininistrator.deleteApplicant.render(applicantList));
        } else if (tab.equalsIgnoreCase("hash-password-user")) {
            List<Users> usersListUnhashed = Users.allUnhashed();
            return ok(views.html.admininistrator.updateHash.render(usersListUnhashed));
        }
        return ok("Page not found");
    }

    public static Result saveSupplied() {
        Form<Supplied> suppliedForm = Form.form(Supplied.class).bindFromRequest();
        Supplied got = suppliedForm.get();
        Supplied supplied = new Supplied();
        Long itemId = Long.parseLong(suppliedForm.field("itemID").value());
        Item item = Item.find(itemId);
        if (item == null) {
            return ok("Item not found");
        }


        supplied.item = item;
        supplied.beginingQty = item.balanceQty;
        Long supplierId = Long.parseLong(suppliedForm.field("supplierID").value());
        supplied.supplier = Supplier.finder.byId(supplierId);
        supplied.suppliedQty = got.suppliedQty;
        supplied.unitPrice = Double.parseDouble(suppliedForm.field("unitPrice").value());
        supplied.fund = suppliedForm.field("fund").value();
        supplied.aquistionDate = got.aquistionDate;
        supplied.expirationDate = got.expirationDate;

        if (got.id != null) {
            Supplied supp = Supplied.find(got.id);
            if (supp == null) {
                return ok("Error occurred");
            }
            int qtyBefore = supp.suppliedQty;
            int qtyNow = got.suppliedQty;
            int qtyToUpdate = supplied.item.balanceQty - qtyBefore + qtyNow;
            item.balanceQty = qtyToUpdate;
            item.purchaseQty = qtyToUpdate;
            item.update();
            supplied.update(supp.id);
        } else {
            item.balanceQty += got.suppliedQty;
            item.purchaseQty += got.suppliedQty;
            supplied.save();
        }

        item.update();
        return ok("1");
    }

    /********
     *
     * Stock movement tabs
     * ********/

    public static Result MovementTabs() {
        return ok(views.html.stock.pages.stockMovementTabs.render());
    }
    public static Result RemoveDepreciated() {
       if(session("Logistic") != null) {
           String s1 = "Requisition to the Chief Budget Manager (CBM) to remove from stock the depreciated asset";
           List<Item> list = Item.finder.
                   where()
                   .eq("iType", "Non-consumable")
                   .raw("DATE(DATE_ADD(date,INTERVAL depleciation_min_months MONTH))<DATE(now())")
                   .findList();
           return ok(views.html.stock.reports.RemoveDepreciated.render(list, s1));
       }
       if(session("Manager") != null) {
           List<Item> list = Item.finder.
                   where()
                   .eq("iType", "Non-consumable")
                   .eq("removed", "pending")
                   .raw("DATE(DATE_ADD(date,INTERVAL depleciation_min_months MONTH))<DATE(now())")
                   .findList();
           String s1 = "Approval to remove from stock the depreciated asset and expired items";
           return ok(views.html.stock.reports.RemoveDepreciatedAprove.render(list, s1));
       }else{
           return ok("");
       }
    }
    public static Result RemoveDefective() {
       if(session("Logistic") != null) {
           String s1 = "Requisition to the Chief Budget Manager (CBM) to remove from stock the expired items";
           List<Item> list = Item.finder.
                   where()
                   .eq("iType", "Consumable")
                   .findList();
           return ok(views.html.stock.reports.RemoveDefective.render(list, s1));
       }
       if(session("Manager") != null) {
           List<Item> list = Item.finder.
                   where()
                   .eq("iType", "Consumable")
                   .eq("status", "defective")
                   .eq("removed", "pending")
                   .findList();
           String s1 = "Approval to remove from stock the defective/expired items";
           return ok(views.html.stock.reports.RemoveDefectiveAprove.render(list, s1));
       }else{
           return ok("");
       }
    }

    public static Result MovementPages(String tab) {
        if (tab.equalsIgnoreCase("in")) {
            List<Supplied> list = Supplied.finder.where()
                    .eq("deleteStatus", false)
                    .eq("item.iType", "Consumable")
                    .findList();
            List<Item> itemList = Item.finder.where()
                    .eq("deleteStatus", false)
                    .eq("iType", "Consumable")
                    .findList();
            return ok(views.html.stock.stockMovements.index.render(list, itemList, Supplier.all()));
        }
        if (tab.equalsIgnoreCase("in-asset")) {
            List<Supplied> list = Supplied.finder.where()
                    .eq("deleteStatus", false)
                    .eq("item.iType", "Non-consumable")
                    .findList();
            List<Item> itemList = Item.finder.where()
                    .eq("deleteStatus", false)
                    .eq("iType", "Non-consumable")
                    .findList();
            return ok(views.html.stock.stockMovements.asset.render(list, itemList, Supplier.all()));
        } else if (tab.equalsIgnoreCase("out")) {
            List<Request> list = Request.finder.where()
                    .eq("deleteStatus", false)
                    .eq("dafStatus", true)
                    .eq("status", "Waiting")
                    .findList();
            return ok(views.html.stock.stockMovements.stockOut.render(list));
        } else if (tab.equalsIgnoreCase("history")) {
            List<StockMovement> list = StockMovement.history();
            List<Item> items = Item.finder.where().eq("deleteStatus", false).eq("iType", "Non-consumable").findList();
            return ok(views.html.stock.stockMovements.history.render(list, Employee.all(), Location.all(), items));
        }
        return ok("Page not found");
    }

    public static Result saveStockOut() {
        return ok();
    }

    /********
     *
     * Staff request tab
     * ********/

    public static Result RequestTabs() {
        return ok(views.html.stock.pages.requestPage.render());
    }

    public static Result RequestPages(String tab) {
        if (tab.equalsIgnoreCase("myItemRequests")) {
            Users u = Users.finderByMail(session(defaultSession));
            if (u.employee == null) {
                return notFound();
            }
            List<StockMovement> list = StockMovement.finder.where()
                    .eq("deleteStatus", false)
                    .eq("request.employee.id", u.employee.id)
                    .eq("request.status", "Processed")
                    .findList();
            return ok(myRequest.render(list));
        }
        return notFound("Page not found");
    }


    public static Result saveExistingAssest() {
        Form<StockMovement> form = Form.form(StockMovement.class).bindFromRequest();
        Employee employee = Employee.finder.where().eq("id", Long.parseLong(form.field("employee").value())).eq("deleteStatus", false).setMaxRows(1).findUnique();
        if (employee == null) {
            return ok("Employee not found");
        }
        Item item = Item.find(Long.parseLong(form.field("item").value()));
        if (item == null) {
            return ok("Item not found");
        }
        Location location = Location.find(Long.parseLong(form.field("location").value()));
        if (location == null) {
            return ok("Location not found");
        }
        Ebean.beginTransaction();
        Request request = new Request();
        request.employee = employee;
        request.headOfUnitStatus = true;
        request.dafStatus = true;
        request.status = "Processed";
        request.save();
        StockMovement movement = new StockMovement();
        movement.request = request;
        movement.confirmedQty = Integer.parseInt(form.field("confirmedQty").value());
        movement.unitPrice = Double.parseDouble(form.field("unitPrice").value());
        movement.item = item;
        movement.location = location;
        movement.employeeStatus = true;
        String depleciationYear = form.field("depleciationYear").value();
        System.out.println(depleciationYear);
        movement.depleciationYear = Integer.valueOf(depleciationYear);
        movement.tagNumber = form.field("tagNumber").value();
        movement.serialNumber = form.field("serialNumber").value();
        movement.iType = "Movement";
        movement.headOfUnitStatus = true;
        movement.dafStatus = true;
        movement.status = true;
        movement.proposedQty = Integer.parseInt(form.field("confirmedQty").value());
        movement.logisticStatus = true;
        movement.save();
        Ebean.commitTransaction();
        return ok("1");
    }

    public static Result updateExistingAssest(long id) {
        Form<StockMovement> form = Form.form(StockMovement.class).bindFromRequest();
        Transaction txn = Ebean.beginTransaction();
        try {
            StockMovement movement = StockMovement.find(id);
            if (movement == null) {
                return notFound();
            }

            Employee employee = Employee.finder.where().eq("id", Long.parseLong(form.field("employee").value())).eq("deleteStatus", false).setMaxRows(1).findUnique();
            if (employee == null) {
                return ok("Employee not found");
            }
            Item item = Item.find(Long.parseLong(form.field("item").value()));
            if (item == null) {
                return ok("Item not found");
            }
            Location location = Location.find(Long.parseLong(form.field("location").value()));
            if (location == null) {
                return ok("Location not found");
            }

            Request request = movement.request;
            request.employee = employee;
            request.headOfUnitStatus = true;
            request.dafStatus = true;
            request.status = "Processed";
            request.update();

            movement.request = request;
            movement.confirmedQty = Integer.parseInt(form.field("confirmedQty").value());
            movement.unitPrice = Double.parseDouble(form.field("unitPrice").value());
            movement.item = item;
            movement.location = location;
            movement.employeeStatus = true;
            String depleciationYear = form.field("depleciationYear").value();
            System.out.println(depleciationYear);
            movement.depleciationYear = Integer.valueOf(depleciationYear);
            movement.tagNumber = form.field("tagNumber").value();
            movement.serialNumber = form.field("serialNumber").value();
            movement.iType = "Movement";
            movement.headOfUnitStatus = true;
            movement.dafStatus = true;
            movement.status = true;
            movement.proposedQty = Integer.parseInt(form.field("confirmedQty").value());
            movement.logisticStatus = true;
            movement.update();

            Ebean.commitTransaction();
            return ok("1");
        } finally {
            txn.end();
        }
    }
}
