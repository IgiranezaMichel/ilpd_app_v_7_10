package services;

import com.avaje.ebean.*;
import models.Payment;
import models.Training;
import play.Logger;
import play.data.Form;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PaymentServices {

    public static Page<Payment> paymentPage(int page, int pageSize, String search) {
        if (search != null && !search.equalsIgnoreCase("")) {
            return Payment.finder.where()
                    .or(
                            Expr.icontains("account.applicant.student.regNo", search),
                            Expr.icontains("status", search)
                    ).orderBy("id desc").findPagingList(pageSize).setFetchAhead(false).getPage(page);
        }
        return Payment.finder.where().orderBy("id desc").findPagingList(pageSize).setFetchAhead(false).getPage(page);
    }

    public static Map<Object, Object> paymentsPages(Form<Payment> form) {
        int start = Integer.parseInt(form.field("start").valueOr("0"));
        int length = Integer.parseInt(form.field("length").valueOr("10"));
        String search = form.field("search[value]").valueOr("").trim();
        String draw = form.field("draw").valueOr("1");


        String sortBy = "id";
        String order = "DESC";
        List<Payment> list;

        int total = Payment.finder
                .where().eq("deleteStatus", false)
                .findRowCount();
        int totalFiltered = total;

        Query<Payment> paymentQuery;
        if (!search.equals("")) {
            paymentQuery = Payment.finder.where()
                    .or(
                            Expr.or(Expr.icontains("account.applicant.familyName", search),
                                    Expr.icontains("account.applicant.firstName", search)),
                            Expr.or(Expr.icontains("account.applicant.student.regNo", search),
                                    Expr.icontains("status", search))
                    )
                    .eq("deleteStatus", false)
                    .orderBy(sortBy + " " + order)
                    .setFirstRow(start)
                    .setMaxRows(length);
            list = paymentQuery.findList();

            totalFiltered = list.size();
        } else {
            paymentQuery = Ebean.find(Payment.class).where()
                    .eq("deleteStatus", false)
                    .orderBy(sortBy + " " + order)
                    .setFirstRow(start)
                    .setMaxRows(length);
            list = paymentQuery.findList();
        }


        Map<Object, Object> map = new HashMap<>();
        map.put("draw", draw);
        map.put("recordsTotal", total);
        map.put("recordsFiltered", totalFiltered);
        map.put("data", list);
        return map;
    }

}
