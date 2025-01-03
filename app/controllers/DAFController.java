package controllers;

import com.avaje.ebean.Ebean;
import models.Users;
import models.stock.Location;
import models.stock.Request;
import models.stock.StockMovement;
import play.data.Form;
import play.mvc.Result;
import views.html.errorPage;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Noel on 14-Mar-18.
 */
public class DAFController extends React
{
    public static Result myRequest()
    {
        List<Request> list = Request.finder.where()
                .eq("deleteStatus", false)
                .eq("status", "pending")
                .eq("headOfUnitStatus", true)
                .eq("dafStatus", false)
                .order("id desc")
                .findList();
        return ok(views.html.stock.DAF.requests.render(list));
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
            }
            if(session("Logistic") != null) {
                user  = Application.Ins("Logistic");
                sendToLogistic();
            }
        Form<StockMovement> form = Form.form(StockMovement.class).bindFromRequest();
        Map<String, String[]> map = request().body().asFormUrlEncoded();
        String[] movements = map.get("stocks[]");
        Ebean.beginTransaction();
        try
        {
            Request request1 = new Request();
            Request request = Request.find(requestId);
            if (request == null)
            {
                Ebean.rollbackTransaction();
                return notFound();
            }
            request1.headOfUnitStatus = request.headOfUnitStatus;
            request1.status = request.status;
            request1.date = request.date;
            request1.employee = request.employee;

            request1.dafStatus = true;
            request1.status = "waiting";
            request1.update(request.id);

            if (movements != null)
            {
                for (String m : movements)
                {
                    Long mId = Long.parseLong(m);
                    StockMovement movement = StockMovement.find(mId);
                    if (movement == null)
                    {
                        return notFound();
                    }
                    movement.dafComment = "";
                    try
                    {
                        int confirmedQty = Integer.parseInt(form.field("confirmedQty" + m).value());
                        int remainQty = Integer.parseInt(form.field("remainQty" + m).value());
                        if(remainQty >= confirmedQty) {
                            movement.confirmedQty = confirmedQty;
                        }
                        if(remainQty > 0 && (remainQty < confirmedQty)){
                            movement.confirmedQty = remainQty;
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        return ok("Number input error");
                    }
                    movement.iType = form.field("iType" + m).value();

                    if (!Objects.equals(form.field("fromLocation" + m).value(), ""))
                    {
                        movement.fromLocation = Location.find(Long.parseLong(form.field("fromLocation" + m).value())).id;
                    }
                    movement.dafStatus = true;
                    movement.doneBy = user.print();
                    movement.update();
                }
            }
            Ebean.commitTransaction();
        }
        finally
        {
            // if commit didn't occur then rollback the transaction
            Ebean.endTransaction();
        }
        return ok("1");
        }else{
            return ok("0");
        }
    }

    private static void sendToLogistic()
    {
        int count = Request.finder.where()
                .eq("deleteStatus",false)
                .eq("status", "pending")
                .eq("dafStatus", true).findRowCount();
        new Counts().sendEmail("Logistic", count);
    }


    public static Result rejectRequest(Long requestId)
    {
        Form<StockMovement> form = Form.form(StockMovement.class).bindFromRequest();
        Map<String, String[]> map = request().body().asFormUrlEncoded();
        String[] movements = map.get("stocks[]");


        Ebean.beginTransaction();
        try
        {

            Request request1 = new Request();
            Request request = Request.find(requestId);
            if (request == null)
            {
                Ebean.rollbackTransaction();
                return notFound();
            }
            request1.headOfUnitStatus = request.headOfUnitStatus;
            request1.status = "Rejected";
            request1.date = request.date;
            request1.employee = request.employee;

            request1.dafStatus = false;
            request1.update(request.id);

            if (movements != null)
            {
                for (String m : movements)
                {
                    Long mId = Long.parseLong(m);
                    StockMovement movement = StockMovement.find(mId);
                    if (movement == null)
                    {
                        return notFound();
                    }
                    movement.dafComment = "";
                    movement.confirmedQty = 0;
                    movement.dafStatus = false;
                    movement.update();
                }
            }
            Ebean.commitTransaction();
        }
        finally
        {
            // if commit didn't occur then rollback the transaction
            Ebean.endTransaction();
        }
        return ok("1");
    }
}
