package controllers;

import com.avaje.ebean.Ebean;
import models.Employee;
import models.Users;
import models.stock.*;
import play.Logger;
import play.data.Form;
import play.libs.Json;
import play.mvc.Result;
import views.html.errorPage;

import javax.persistence.OptimisticLockException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Noel on 14-Mar-18.
 */
public class HUnitController extends React
{
    public static Result myRequest()
    {
        Users user = Users.finderByMail(session(defaultSession));
        if (user == null)
        {
            return ok("error");
        }
        if (user.employee == null)
        {
            return ok("Error.");
        }
        List<Request> requests = Request.finder.where()
                .eq("deleteStatus", false)
                .eq("status", "pending")
                .eq("headOfUnitStatus", false)
                .eq("employee.unit.id", user.employee.unit.id)
                .order("id desc").findList();
        return ok(views.html.stock.HeadOfUnit.requests.render(requests));
    }
    public static Result myRequestDaf()
    {
        Users user = Users.finderByMail(session(defaultSession));
        if (user == null)
        {
            return ok("error");
        }
        if (user.employee == null)
        {
            return ok("Error.");
        }
        List<Request> requests = Request.finder.where()
                .eq("deleteStatus", false)
                .eq("status", "pending")
                .eq("headOfUnitStatus", false)
                .order("id desc").findList();
        return ok(views.html.stock.HeadOfUnit.requests.render(requests));
    }

    public static Result approveRequest(Long requestId)
    {
        if (session("HeadOfUnit") != null || session("DAF") != null || session("Logistic") != null) {
            Users user = null;
            if(session("HeadOfUnit") != null) {
                user  = Application.Ins("HeadOfUnit");
            }
            if(session("DAF") != null) {
                user  = Application.Ins("DAF");
                sendDaf();
            }
            if(session("Logistic") != null) {
                user  = Application.Ins("Logistic");
            }
        Form<StockMovement> form = Form.form(StockMovement.class).bindFromRequest();

        Map<String, String[]> map = request().body().asFormUrlEncoded();
        String[] movements = map.get("stocks[]");

        Ebean.beginTransaction();
        Request request = Request.find(requestId);
        if (request == null)
        {
            Ebean.rollbackTransaction();
            return notFound();
        }
        request.headOfUnitStatus = true;
        request.dafStatus = false;
        request.update();

        for (String m : movements)
        {
            Long mId = Long.parseLong(m);
            StockMovement movement = StockMovement.find(mId);
            if (movement == null)
            {
                return notFound();
            }
            movement.headOfUnitComment = "";
            movement.confirmedQty = Integer.parseInt(form.field("confirmedQty" + m).value());
            movement.iType = form.field("type" + m).value();
            if (!form.field("fromLocation" + m).value().equals(""))
            {
                movement.fromLocation = Location.find(Long.parseLong(form.field("fromLocation" + m).value())).id;
            }
            movement.headOfUnitStatus = true;
            movement.doneBy = user.print();
            movement.update();
        }
        Ebean.commitTransaction();
        return ok();
        }else{
            return ok("0");
        }
    }
    private static void sendDaf()
    {
        int count = Request.finder.where()
                .eq("deleteStatus",false)
                .eq("status", "pending")
                .eq("headOfUnitStatus", true).findRowCount();
       new Counts().sendEmail("DAF", count);
    }

    public static Result details(Long id, String info)
    {

        if (info.equalsIgnoreCase("getRequest"))
        {
            Request request = Request.find(id);
            if (request == null)
            {
                return notFound();
            }
            return ok(views.html.stock.HeadOfUnit.requestDetails.render(request, Location.all()));
        }
        else if (info.equalsIgnoreCase("dafGetRequest"))
        {
            Request request = Request.find(id);
            if (request == null)
            {
                return notFound();
            }
            return ok(views.html.stock.DAF.confirmRequest.render(request, Location.all()));
        }
        else if (info.equalsIgnoreCase("logisticGetRequest"))
        {
            Request request = Request.find(id);
            if (request == null)
            {
                return notFound();
            }
            return ok(views.html.stock.stockMovements.details.render(request));
        }
        return ok("Error.. the page you trying to look is no longer exist");
    }


    public static Result employeeEnfirmation(Long id)
    {
        Form<StockMovement> form = Form.form(StockMovement.class).bindFromRequest();

        Map<String, String[]> map = request().body().asFormUrlEncoded();
        String[] movements = map.get("movements[]");


        Ebean.beginTransaction();
        Request request = Request.find(id);
        Request request1 = new Request();
        if (request == null)
        {
            Ebean.rollbackTransaction();
            return notFound();
        }

        request1.status = "Waiting";
        request1.dafStatus = request.dafStatus;
        request1.headOfUnitStatus = request.headOfUnitStatus;
        request1.update(request.id);

        for (String m : movements)
        {
            Long mId = Long.parseLong(m);
            StockMovement movement = StockMovement.find(mId);
            if (movement == null)
            {
                Ebean.rollbackTransaction();
                return notFound();
            }
            movement.employeeStatus = true;
            String s = form.field("toLocation" + movement.id).value();
            if(s.equalsIgnoreCase("")) {
                movement.toLocation = Location.find(Long.parseLong(s)).id;
            }
            movement.update();
        }
        Ebean.commitTransaction();
        return ok();
    }
    public static Result comfirmStockOut(Long id, String s) {
        Form<StockMovement> form = Form.form(StockMovement.class).bindFromRequest();
        Ebean.beginTransaction();
        try {
            Map<String, String[]> map = request().body().asFormUrlEncoded();
            String[] movements = map.get("movements[]");

            Request request = Request.find(id);
            if (request == null) {
                Ebean.rollbackTransaction();
                return notFound();
            }
            Request request1 = (Request) request._ebean_createCopy();
            request1.employee = request.employee;
            request1.date = request.date;
            request1.dafStatus = request.dafStatus;
            request1.headOfUnitStatus = request.headOfUnitStatus;
            request1.deleteStatus = request.deleteStatus;
            request1.status = "Processed";
            request1.update(request.id);
            List<StockMovement> stockMovements = new ArrayList<>();
            for (String m : movements)
            {
                StockMovement stockMovement = StockMovement.find(Long.parseLong(m));
                StockMovement movement = new StockMovement();
                if (stockMovement == null)
                {
                    Ebean.rollbackTransaction();
                    return notFound();
                }
                String iType = stockMovement.iType;
                iType = iType == null ? "" : iType;
                if ( iType.equalsIgnoreCase("Stock"))
                {
                    try
                    {
                        if (s.equalsIgnoreCase("2"))
                        {
                            Item item = Item.find(stockMovement.item.id);
                            item.balanceQty = item.balanceQty - stockMovement.confirmedQty;
                            item.update();
                            Logger.debug("Updated:" + item.balanceQty);
                        }
                    } catch (OptimisticLockException e)
                    {
                        e.printStackTrace();
                    }
                }
               // int remain = Integer.parseInt(form.field("remain_"+movement.id).value());
                movement.logisticStatus = true;
                movement.request = stockMovement.request;
                movement.proposedQty = stockMovement.proposedQty;
                movement.confirmedQty = stockMovement.confirmedQty;
                movement.headOfUnitStatus = stockMovement.headOfUnitStatus;
                movement.employeeStatus = stockMovement.employeeStatus;
                movement.iType = iType;
              //  movement.fromLocation = stockMovement.fromLocation;
              //  movement.toLocation = stockMovement.toLocation;
                movement.dafStatus = stockMovement.dafStatus;

             //   Location location = Location.find(Long.parseLong(form.field("toLocation" + m).value()));
              //  if (location == null)
              //  {
              //      Ebean.rollbackTransaction();
               //     return notFound();
              //  }
              //  movement.location = location;

                String type = movement.iType;

                type = type == null ? "" : type;

                if (type.equalsIgnoreCase("Movement"))
                {
                    StockMovement mvnt = StockMovement.finder.where().eq("location.id", movement.fromLocation).setMaxRows(1).findUnique();
                    if (mvnt == null)
                    {
                        Ebean.rollbackTransaction();
                        return notFound();
                    }
                    movement.serialNumber = mvnt.serialNumber;
                    movement.tagNumber = mvnt.tagNumber;
                    movement.depleciationYear = mvnt.depleciationYear;
                    mvnt.location = null;
                    mvnt.fromLocation = null;
                    mvnt.toLocation = null;
                    mvnt.update();
                }
                if (form.field("tagNumber" + stockMovement.id).value() != null)
                {
                    movement.tagNumber = form.field("tagNumber" + stockMovement.id).value();
                    movement.depleciationYear = Integer.parseInt(form.field("depleciationYear" + stockMovement.id).value());
                    movement.serialNumber = form.field("serialNumber" + stockMovement.id).value();
                }
                movement.status = true;
                stockMovements.add(movement);
                movement.update(stockMovement.id);
            }
            Ebean.commitTransaction();
            return ok(views.html.stock.requests.requestConfirmation.render(stockMovements, request));
        } finally
        {
            Ebean.endTransaction();
        }
    }

    public static Result comfirmStockOutHarmonization() {
        Form<StockMovement> form = Form.form(StockMovement.class).bindFromRequest();
        StockMovement stockMovement1 = form.get();
            Request request2 = new Request();
            Employee employee = Employee.finder.where()
                    .eq("employeeFirstName", "Harmonization")
                    .findUnique();
            request2.employee = employee;
            request2.date = new Date();
            request2.dafStatus = true;
            request2.headOfUnitStatus = true;
            request2.status = "Processed";
            request2.save();
            StockMovement stockMovementt = new StockMovement();
            stockMovementt.logisticStatus = true;
            stockMovementt.request = request2;
            stockMovementt.proposedQty = Integer.parseInt(form.field("suppliedQty").value());
            stockMovementt.confirmedQty = Integer.parseInt(form.field("suppliedQty").value());
            stockMovementt.headOfUnitStatus = true;
            stockMovementt.employeeStatus = true;
            Item item = models.stock.Item.find(Long.parseLong(form.field("itemID").value()));
            stockMovementt.iType = item.iType;
            stockMovementt.item = item;
            stockMovementt.dafStatus = true;
            stockMovementt.save();
            return ok("1");
    }
    public static Result comfirmDeleteItem() {
        Form<Item> form = Form.form(Item.class).bindFromRequest();
        Item item = form.get();
        Long id = Long.parseLong(form.field("itemID").value());
        Item item1 = Item.finder.byId(id);
        item1.deleteStatus = true;
        item1.update();
        return ok("1");
    }
}
