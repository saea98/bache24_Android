package com.cmi.bache24.data.remote;

import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;

import com.cmi.bache24.data.model.AttendedReport;
import com.cmi.bache24.data.model.Comment;
import com.cmi.bache24.data.model.Picture;
import com.cmi.bache24.data.model.PushRecord;
import com.cmi.bache24.data.model.Rejection;
import com.cmi.bache24.data.model.Report;
import com.cmi.bache24.data.model.Status;
import com.cmi.bache24.data.model.User;
import com.cmi.bache24.data.remote.interfaces.CommentsCallback;
import com.cmi.bache24.data.remote.interfaces.IGetVersionCallback;
import com.cmi.bache24.data.remote.interfaces.LoginCallback;
import com.cmi.bache24.data.remote.interfaces.NewReportCallback;
import com.cmi.bache24.data.remote.interfaces.RecoverPasswordCallback;
import com.cmi.bache24.data.remote.interfaces.RegisterUserCallback;
import com.cmi.bache24.data.remote.interfaces.RejectionsCallback;
import com.cmi.bache24.data.remote.interfaces.ReportsCallback;
import com.cmi.bache24.data.remote.interfaces.SingleReportCallback;
import com.cmi.bache24.ui.dialog.interfaces.IStatusResponse;
import com.cmi.bache24.util.Constants;
import com.cmi.bache24.util.FileManager;
import com.cmi.bache24.util.ReportComparator;
import com.cmi.bache24.util.TroopReportComparator;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Created by omar on 12/2/15.
 */
public class ServicesManager {

    public static final int TIMEOUT = 30 * 1000; // 30 Segundos

    public static void registerUser(final User user, final RegisterUserCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(TIMEOUT);

        final String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_REGISTER;

        RequestParams params = new RequestParams();

        params.put("usuario_usuario", user.getEmail());
        params.put("usuario_nombre", user.getName());
        params.put("usuario_apellido_paterno", user.getFirtsLastName());
        params.put("usuario_apellido_materno", user.getSecondLastName());
        params.put("usuario_telefono", user.getPhone());
        params.put("usuario_correo", user.getEmail());
        params.put("usuario_avatar", user.getPicture());

        if (user.getRegisterType().equals(Constants.REGISTER_EMAIL)) {

            params.put("usuario_contrasenia", user.getPassword());

        } else if (user.getRegisterType().equals(Constants.REGISTER_FACEBOOK)) {

            params.put("usuario_fb", user.getFbUsername());
            params.put("usuario_fb_token", user.getFbToken());
            params.put("usuario_hash", user.getHashToken());

        } else if (user.getRegisterType().equals(Constants.REGISTER_TWITTER)) {

            params.put("usuario_tw", user.getTwUsername());
            params.put("usuario_tw_token", user.getTwToken());
            params.put("usuario_hash", user.getHashToken());
        }

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                printResponse(response.toString());

                if (isBannedUser(response)) {

                    if (callback != null)
                        callback.userBanned();

//                    sendDebugResponse(user.getEmail(), url, "", response.toString(), user.getToken());

                    return;
                }

                if (response.has(Constants.RESPONSE_ERROR_KEY)) {
                    try {
                        if (callback != null)
                            callback.onRegisterFail(getMessageForResponseCode(response.getString(Constants.RESPONSE_ERROR_KEY)));
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onRegisterFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                } else if (response.has("auth_token")) {
                    try {
                        user.setToken(response.getString("auth_token").toString());

                        if (response.has("avatar")) {
                            user.setPicture(response.getString("avatar"));
                            user.setPictureUrl(response.getString("avatar"));
                        }

                        if (callback != null)
                            callback.onRegisterSuccess(user);
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onRegisterFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                } else {
                    callback.onRegisterFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

                if (callback != null)
                    callback.onRegisterFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable.getMessage() != null) {
                    if (throwable.getMessage().contains("timed out")) {
                        if (callback != null)
                            callback.onRegisterFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onRegisterFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onRegisterFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                /*if (throwable.getMessage() != null) {
                    if (throwable.getMessage().contains("timed out")) {
                        if (callback != null)
                            callback.onRegisterFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onRegisterFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onRegisterFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable.getMessage() != null) {
                    if (throwable.getMessage().contains("timed out")) {
                        if (callback != null)
                            callback.onRegisterFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onRegisterFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onRegisterFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }
        });
    }

    public static void loginWithUser(final User user, final LoginCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(TIMEOUT);

        final String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_LOGIN;

        RequestParams params = new RequestParams();
        params.put("usuario_usuario", user.getEmail() != null ? user.getEmail() : "" );
        params.put("usuario_contrasenia", user.getPassword() != null ? user.getPassword() : "");
        params.put("usuario_hash", user.getHashToken() != null ? user.getHashToken().replace("\n", "") : "");
        params.put("usuario_fb_token", user.getFbToken() != null ? user.getFbToken() : "");
        params.put("usuario_tw_token", user.getTwToken() != null ? user.getTwToken() : "");

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                printResponse(response.toString());

                if (isBannedUser(response)) {
                    if (callback != null)
                        callback.userBanned();

//                    sendDebugResponse(user.getEmail(), url, "", response.toString(), user.getToken());

                    return;
                }

                if (response.has(Constants.RESPONSE_ERROR_KEY)) {
                    try {
                        if (callback != null)
                            callback.loginFail(getMessageForResponseCode(response.getString(Constants.RESPONSE_ERROR_KEY)));
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.loginFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                } else {
                    try {

                        User userInfo = new User();

                        userInfo.setName(response.getString("nombre"));
                        userInfo.setFirtsLastName(response.getString("apellidoPa"));
                        userInfo.setSecondLastName(response.getString("apellidoMa"));
                        userInfo.setPhone(response.getString("telefono"));
                        userInfo.setEmail(response.getString("correo"));
                        userInfo.setUserType(response.getInt("tipo_usuario"));
                        userInfo.setPicture(response.getString("avatar"));
                        userInfo.setPictureUrl(response.getString("avatar"));
                        userInfo.setIdDelegacion(response.getInt("delegacion"));
                        userInfo.setToken(response.getString("auth_token"));

                        userInfo.setHashToken(user.getHashToken() != null ? user.getHashToken().replace("\n", "") : "");
                        userInfo.setFbToken(user.getFbToken() != null ? user.getFbToken() : "");
                        userInfo.setTwToken(user.getTwToken() != null ? user.getTwToken() : "");

                        if (callback != null)
                            callback.loginSuccess(userInfo);
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.loginFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                if (callback != null)
                    callback.loginFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

                if (callback != null)
                    callback.loginFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.loginFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.loginFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                callback.loginFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.loginFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.loginFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                callback.loginFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.loginFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.loginFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                callback.loginFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }
        });
    }

    public static void recoverPassword(final User user, final RecoverPasswordCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(TIMEOUT);

        final String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_RECOVER_PASSWORD;

        RequestParams params = new RequestParams();
        params.put("usuario_correo", user.getEmail());

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                printResponse(response.toString());

                if (isBannedUser(response)) {
                    if (callback != null)
                        callback.userBanned();

//                    sendDebugResponse(user.getEmail(), url, "", response.toString(), user.getToken());

                    return;
                }

                if (response.has("OK")) {
                    if (callback != null)
                        callback.onRecoverSuccess();
                } else {
                    if (callback != null)
                        callback.onRecoverFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (callback != null)
                    callback.onRecoverFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                if (callback != null)
                    callback.onRecoverFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onRecoverFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onRecoverFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onRecoverFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onRecoverFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onRecoverFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onRecoverFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onRecoverFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onRecoverFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onRecoverFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }
        });
    }

    public static void registerNewReport(final User user, final Report report, final NewReportCallback callback) {

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(TIMEOUT);

        final String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_REGISTER_REPORT;

        RequestParams params = new RequestParams();

        client.addHeader("token", user.getToken());

        params.put("bache_longitud", report.getLongitude());
        params.put("bache_latitud", report.getLatitude());
        params.put("bache_descripcion", report.getDescription());
        params.put("bache_vialidad_numero", report.getNumero());
        params.put("bache_colonia", report.getColonia());
        params.put("bache_direccion", report.getAddress());
        params.put("bache_vialidad", report.getVialidad());
        params.put("bache_img1", report.getPicture1());
        params.put("bache_img2", report.getPicture2());
        params.put("bache_img3", report.getPicture3());
        params.put("bache_img4", report.getPicture4());

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if (isBannedUser(response)) {
                    if (callback != null)
                        callback.userBanned();

//                    sendDebugResponse(user.getEmail(), url, "", response.toString(), user.getToken());

                    return;
                }

                if (isTokenDisabled(response)) {
                    if (callback != null)
                        callback.onTokenDisabled();

                    return;
                }

                if (response.has("OK")) {
                    try {
                        report.setTicket(response.getString("ID").toString().trim());

                        if (response.has("id_cat_delegacion")) {
                            report.setDelegationId(response.getInt("id_cat_delegacion"));
                        }

                        report.setReportVersion(response.has("vialidad") ? response.getInt("vialidad") : Constants.NEW_VERSION);

                        report.setRoadType(response.has("primaria") ? response.getInt("primaria") : Constants.ROAD_TYPE_DEFAULT);

                        if (callback != null)
                            callback.onSuccessRegister(response.getString("OK"), report);
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                } else if (response.has("ERROR")) {

                    try {
                        if (response.getString("ERROR").equals(Constants.NEW_REPORT_RESULT_OUT_OF_RANGE) ||
                            response.getString("ERROR").equals(Constants.NEW_REPORT_RESULT_ALREADY_REPORTED)) {

                            report.setTicket(response.getString("ERROR").toString().trim());

                            if (callback != null)
                                callback.onSuccessRegister(response.getString("ERROR"), report);
                        } else {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                        }
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }

                } else {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onFailRegisterReport(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onFailRegisterReport(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onFailRegisterReport(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }
        });
    }

    private static boolean timerFinished;

    public static void getReports(final User user, final ReportsCallback callback) {
        final AsyncHttpClient client = new AsyncHttpClient();

//        client.setTimeout(TIMEOUT);
        client.setTimeout(50000);

        final String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_GET_REPORTS;
//        final String url = "http://www.google.com:81/";

        RequestParams params = new RequestParams();

        client.addHeader("token", user.getToken());

        timerFinished = false;

        final CountDownTimer timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                client.cancelAllRequests(true);

                if (timerFinished) {
//                    Log.i("BACHE_TIMEOUT", "timer already finished");

                    return;
                }

//                Log.i("BACHE_TIMEOUT", "timer finished = " + new Date().toString());

                if (callback != null)
                    callback.onReportsFail(Constants.TIMEOUT_DESCRIPTION);

                return;
            }
        };

//        Log.i("BACHE_TIMEOUT", "timer started = " + new Date().toString());

        timer.start();

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                timer.cancel();
                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "Success = " + new Date().toString());

                if (isBannedUser(response)) {
                    if (callback != null)
                        callback.userBanned();

//                    sendDebugResponse(user.getEmail(), url, "", response.toString(), user.getToken());

                    return;
                }

                if (isTokenDisabled(response)) {
                    if (callback != null)
                        callback.onTokenDisabled();

                    return;
                }

                if (response.has(Constants.RESPONSE_ERROR_KEY)) {
                    try {
                        if (callback != null)
                            callback.onReportsFail(getMessageForResponseCode(response.getString(Constants.RESPONSE_ERROR_KEY)));
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onReportsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                } else {
                    try {

                        List<Report> reports = new ArrayList<>();

                        if (response.has("OK")) {
                            if (callback != null)
                                callback.onReportsCallback(reports);
                        } else {
                            Iterator<String> iterator = response.keys();
                            while (iterator.hasNext()) {
                                String key = iterator.next();

                                Report report = new Report();
                                JSONObject jsonObject = (JSONObject) response.get(key);

                                report.setReportId(jsonObject.getString("id_bache"));
                                report.setDate(jsonObject.getString("bache_fecha_alta"));
                                report.setTicket(jsonObject.getString("bache_ticket"));
                                report.setEtapaId(jsonObject.getInt("id_cat_etapa"));
                                report.setLongitude(jsonObject.getString("bache_longitud"));
                                report.setLatitude(jsonObject.getString("bache_latitud"));
                                report.setDescription(jsonObject.getString("bache_descripcion"));
                                report.setColonia(jsonObject.getString("bache_colonia"));
                                report.setAddress(jsonObject.getString("bache_direccion"));
                                report.setAvenidaId(jsonObject.getInt("id_cat_avenida"));

                                if (jsonObject.has("bache_ticket_sales")) {
                                    report.setSalesForceTicket(jsonObject.getString("bache_ticket_sales"));
                                }

                                if (jsonObject.has("id_cat_delegacion")) {
                                    report.setDelegationId(jsonObject.getInt("id_cat_delegacion"));
                                }

                                report.setReportVersion(jsonObject.has("vialidad") ? jsonObject.getInt("vialidad") : Constants.NEW_VERSION);
                                report.setCause(jsonObject.has("razon_no_aplica") ? jsonObject.getString("razon_no_aplica") : "");

                                report.setPictures(new ArrayList<Picture>());

                                if (jsonObject.has("imgs")) {
                                    JSONArray images = jsonObject.getJSONArray("imgs");

                                    if (images.length() > 0) {
                                        for (int i = 0; i < images.length(); i++) {
                                            Picture reportPicture = new Picture();
                                            reportPicture.setEtapaId(images.getJSONObject(i).getInt("id_cat_etapa"));
                                            reportPicture.setFoto(images.getJSONObject(i).getString("foto"));

                                            report.getPictures().add(reportPicture);
                                        }
                                    }
                                }

                                report.setPushHistory(new ArrayList<PushRecord>());

                                try {
                                    if (jsonObject.has("push_historial")) {
                                        JSONArray pushHistoryArray = jsonObject.getJSONArray("push_historial");
                                        for (int i = 0; i < pushHistoryArray.length(); i++) {
                                            PushRecord record = new PushRecord();

                                            record.setMessage(pushHistoryArray.getJSONObject(i).getString("push_mensaje"));
                                            record.setDate(pushHistoryArray.getJSONObject(i).getString("push_fecha_envio"));

                                            report.getPushHistory().add(record);
                                        }
                                    }
                                } catch (Exception ex) {

                                }

                                reports.add(report);
                            }

                            Collections.sort(reports, new ReportComparator());

                            if (callback != null)
                                callback.onReportsCallback(reports);
                        }
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onReportsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                timer.cancel();
                timerFinished = true;

                if (callback != null)
                    callback.onReportsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

                timer.cancel();
                timerFinished = true;

                if (callback != null)
                    callback.onReportsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onReportsFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                timer.cancel();
                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onReportsFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onReportsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onReportsFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                timer.cancel();
                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "Failure2 = " + new Date().toString());

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onReportsFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onReportsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onReportsFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                timer.cancel();
                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "Failure3 = " + new Date().toString());

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onReportsFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onReportsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }
        });
    }

    public static void updateUser(final User user, final RegisterUserCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(TIMEOUT);

        final String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_UPDATE_USER;

        RequestParams params = new RequestParams();
        params.put("usuario_usuario", user.getEmail());
        params.put("usuario_contrasenia", user.getOldPassword());
        params.put("usuario_contrasenia_cambio", user.getPassword());
        params.put("usuario_nombre", user.getName());
        params.put("usuario_apellido_paterno", user.getFirtsLastName());
        params.put("usuario_apellido_materno", user.getSecondLastName());
        params.put("usuario_telefono", user.getPhone());
        params.put("usuario_correo", user.getEmail());
        params.put("usuario_fb", user.getFbUsername());
        params.put("usuario_fb_token", user.getFbToken());
        params.put("usuario_tw", user.getTwUsername());
        params.put("usuario_tw_token", user.getTwToken());
        params.put("usuario_hash", user.getHashToken());
        params.put("usuario_avatar", user.getPicture());

        client.addHeader("token", user.getToken());

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if (isBannedUser(response)) {
                    if (callback != null)
                        callback.userBanned();

//                    sendDebugResponse(user.getEmail(), url, "", response.toString(), user.getToken());

                    return;
                }

                if (isTokenDisabled(response)) {
                    if (callback != null)
                        callback.onTokenDisabled();

                    return;
                }

                if (response.has("OK")) {

                    try {
                        if (response.has("url_foto"))
                            user.setPictureUrl(response.getString("url_foto"));
                    } catch (JSONException ex) {

                    }

                    if (callback != null)
                        callback.onRegisterSuccess(user);

                } else {

                    if (response.has(Constants.RESPONSE_ERROR_KEY)) {
                        try {
                            if (callback != null)
                                callback.onRegisterFail(getMessageForResponseCode(response.getString(Constants.RESPONSE_ERROR_KEY)));
                        } catch (JSONException ex) {
                            if (callback != null)
                                callback.onRegisterFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                        }
                    } else
                        callback.onRegisterFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);

                    if (callback != null) {
                        if (callback != null)
                            callback.onRegisterSuccess(user);
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (callback != null)
                    callback.onRegisterFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                if (callback != null)
                    callback.onRegisterFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onRegisterFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onRegisterFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onRegisterFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onRegisterFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onRegisterFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onRegisterFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onRegisterFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onRegisterFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onRegisterFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }
        });
    }

    public static void getReportsForTroops(User user, final ReportsCallback callback) {
        final AsyncHttpClient client = new AsyncHttpClient();
//        client.setTimeout(TIMEOUT);
        client.setTimeout(60 * 1000);
        client.setConnectTimeout(60 * 1000);
        client.setResponseTimeout(60 * 1000);

        String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_GET_TROOPS_REPORTS;

        RequestParams params = new RequestParams();

        client.addHeader("token", user.getToken());

        timerFinished = false;

        final CountDownTimer timer = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                client.cancelAllRequests(true);

                if (timerFinished) {
//                    Log.i("BACHE_TIMEOUT", "timer already finished");

                    return;
                }

//                Log.i("BACHE_TIMEOUT", "timer finished = " + new Date().toString());

                if (callback != null)
                    callback.onReportsFail(Constants.TIMEOUT_DESCRIPTION);

                return;
            }
        };

//        Log.i("BACHE_TIMEOUT", "timer started = " + new Date().toString());

        timer.start();

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Success = " + new Date().toString());

                if (isTokenDisabled(response)) {
                    if (callback != null)
                        callback.onTokenDisabled();

                    return;
                }

                if (response.has(Constants.RESPONSE_ERROR_KEY)) {
                    try {
                        if (callback != null) {
                            if (Integer.valueOf(response.getString(Constants.RESPONSE_ERROR_KEY)) == Constants.ERROR_NO_REPORTS_FOUND) {
                                callback.onReportsFail(Constants.ERROR_NO_REPORTS_FOUND_DESCRIPTION_2);
                            } else
                                callback.onReportsFail(getMessageForResponseCode(response.getString(Constants.RESPONSE_ERROR_KEY)));
                        }
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onReportsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                } else {
                    try {

                        List<Report> reports = new ArrayList<>();

                        if (response.has("OK")) {
                            if (callback != null)
                                callback.onReportsCallback(reports);
                        } else {
                            Iterator<String> iterator = response.keys();

                            SimpleDateFormat mDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);

                            while (iterator.hasNext()) {
                                String key = iterator.next();

                                Report report = new Report();
                                JSONObject jsonObject = (JSONObject) response.get(key);

                                report.setReportId(jsonObject.getString("id_bache"));
                                report.setDate(jsonObject.getString("bache_fecha_alta"));
                                report.setTicket(jsonObject.getString("bache_ticket"));
                                report.setEtapaId(jsonObject.getInt("id_cat_etapa"));
                                report.setLongitude(jsonObject.getString("bache_longitud"));
                                report.setLatitude(jsonObject.getString("bache_latitud"));

                                report.setDescription(jsonObject.getString("bache_descripcion"));
                                report.setColonia(jsonObject.getString("bache_colonia"));
                                report.setAddress(jsonObject.getString("bache_direccion"));

                                report.setOrigin(getOrigin(jsonObject.has("bache_lugar_alta") ? jsonObject.getInt("bache_lugar_alta") : Constants.REPORT_ORIGIN_APP));

                                if (jsonObject.has("bache_urgente")) {
                                    report.setUrgent(jsonObject.getBoolean("bache_urgente"));
                                }

                                report.setVialidad(jsonObject.has("bache_vialidad") ? jsonObject.getString("bache_vialidad") : "");

                                if (jsonObject.has("bache_secundaria")) {
                                    report.setHasPrimary(true);
                                    String primaryValue = jsonObject.getString("bache_secundaria");
                                    if (primaryValue == null) {
                                        report.setPrimary(false);
                                    } else {
                                        report.setPrimary((primaryValue.equals(Constants.PRIMARY_YES) ||
                                                            primaryValue == Constants.PRIMARY_YES));
                                    }
                                }

                                Date date1 = null;
                                try {
                                    date1 = mDateFormat.parse(report.getDate());
                                } catch (ParseException ex) {
                                    date1 = new Date();
                                }

                                if (report.isUrgent()) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date1);
                                    calendar.add(Calendar.HOUR_OF_DAY, -19);
                                    date1 = calendar.getTime();
                                }

                                report.setCreationDate(date1);

                                JSONObject jsonFotos = jsonObject.getJSONObject("fotos");

                                JSONArray images = jsonFotos.getJSONArray("imgs");

                                report.setPictures(new ArrayList<Picture>());

                                if (images.length() > 0) {
                                    for (int i = 0; i < images.length(); i++) {
                                        Picture reportPicture = new Picture();
                                        reportPicture.setEtapaId(images.getJSONObject(i).getInt("id_cat_etapa"));
                                        reportPicture.setFoto(images.getJSONObject(i).getString("foto"));

                                        report.getPictures().add(reportPicture);
                                    }
                                }

//                                if (report.getEtapaId() != Constants.TROOP_REPORT_STATUS_MARK_AS_REPAIRED &&
//                                    report.getEtapaId() != Constants.TROOP_REPORT_STATUS_DOES_NOT_APPLY)

                                if (report.getEtapaId() == Constants.REPORT_STATUS_NEW ||
                                    report.getEtapaId() == Constants.REPORT_STATUS_7 ||
                                    report.getEtapaId() == Constants.REPORT_STATUS_ASIGNED) {
                                    reports.add(report);
                                }
                            }
                        }

                        Collections.sort(reports, new TroopReportComparator());
                        if (callback != null)
                            callback.onReportsCallback(reports);
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onReportsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                if (timerFinished) {
                    client.cancelAllRequests(true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

                if (callback != null)
                    callback.onReportsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

                if (timerFinished) {
                    client.cancelAllRequests(true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

                if (callback != null)
                    callback.onReportsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (timerFinished) {
                    client.cancelAllRequests(true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onReportsFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onReportsFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onReportsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                if (timerFinished) {
                    client.cancelAllRequests(true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onReportsFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onReportsFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onReportsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (timerFinished) {
                    client.cancelAllRequests(true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onReportsFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onReportsFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onReportsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }
        });
    }

    public static void updateReportStatus(final Context context,
                                          final User user,
                                          String reportTicket,
                                          int status,
                                          final NewReportCallback callback) {

        final AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(TIMEOUT);

        String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_UPDATE_REPORT_BY_TROOP;

        RequestParams params = new RequestParams();

        client.addHeader("token", user.getToken());

        params.put("bache_ticket", reportTicket);
        params.put("id_cat_etapa", status);

//        Log.i("BACHE_SERVICES", "updateReportStatus (set status 2) executed for ticket = " + reportTicket);

        final Report currentReport = new Report();
        currentReport.setTicket(reportTicket);
        currentReport.setEtapaId(status);

        /*B24Debug debugObject = currentReport.debugObject();
        debugObject.setReportSquadEmail(user.getEmail());
        debugObject.setReportSquadToken(user.getToken());

        ServicesManager.sendDebugData(debugObject);*/

//        Utils.Log("ServicesManager", "updateReportStatus", "Service call for " + reportTicket);

        timerFinished = false;

        final CountDownTimer timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                client.cancelRequests(context, true);

                if (timerFinished) {
//                    Log.i("BACHE_TIMEOUT", "timer already finished");

                    return;
                }

                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "timer finished = " + new Date().toString());

                if (callback != null)
                    callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                return;
            }
        };

//        Log.i("BACHE_TIMEOUT", "timer started = " + new Date().toString());

        timer.start();

        client.post(context, url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                timer.cancel();
                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "success = " + new Date().toString());

//                Utils.Log("ServicesManager", "onSuccess", "Service response " + response.toString());

                /*String message = ServicesManager.getJSONStringWithParameters(currentReport.getTicket(),
                        user.getEmail(),
                        response != null ? response.toString() : "Response Null");
                ServicesManager.sendDebugThrowableDataWithToken(currentReport, null, message, statusCode + "", user.getToken());*/

                if (response.has("OK")) {
                    try {

                        if (response.getString("OK").equals("805") ||
                                response.getString("OK").equals("806") ||
                                response.getString("OK").equals("807")) {
                            if (callback != null){
                                callback.onSuccessRegisterReport(null);
                            }
                        } else
                            callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);

                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                } else {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

//                Utils.Log("ServicesManager", "onFailure", "Service response failure " + errorResponse.toString());

                /*String message = ServicesManager.getJSONStringWithParameters(currentReport.getTicket(),
                        user.getEmail(),
                        errorResponse != null ? errorResponse.toString() : "Response Null");
                ServicesManager.sendDebugThrowableData(currentReport, throwable, message, statusCode + "");*/

                /*if (throwable != null) {

                    if (throwable.getMessage() != null) {
                        if (throwable.getMessage().contains("timed out")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    } else {
                        if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

//                Utils.Log("ServicesManager", "onFailure", "Service response failure " + responseString);

                /*String message = ServicesManager.getJSONStringWithParameters(currentReport.getTicket(),
                        user.getEmail(),
                        responseString != null ? responseString : "Response Null");
                ServicesManager.sendDebugThrowableData(currentReport, throwable, message, statusCode + "");*/

                /*if (throwable != null) {

                    if (throwable.getMessage() != null) {
                        if (throwable.getMessage().contains("timed out")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    } else {
                        if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

//                Utils.Log("ServicesManager", "onFailure", "Service response failure " + errorResponse.toString());

                /*String message = ServicesManager.getJSONStringWithParameters(currentReport.getTicket(),
                        user.getEmail(),
                        errorResponse != null ? errorResponse.toString() : "Response Null");
                ServicesManager.sendDebugThrowableData(currentReport, throwable, message, statusCode + "");*/

                /*if (throwable != null) {

                    if (throwable.getMessage() != null) {
                        if (throwable.getMessage().contains("timed out")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    } else {
                        if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);
            }
        });
    }

    public static void getSingleReport(final User user, final Report report, final SingleReportCallback callback) {

        if (user == null) {
            if (callback != null)
                callback.reportFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);

            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(TIMEOUT);

        final String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_GET_REPORT_DETAIL;

        RequestParams params = new RequestParams();

        client.addHeader("token", user.getToken());

        params.add("bache_ticket", report.getTicket());

        client.post(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if (isBannedUser(response)) {
                    if (callback != null)
                        callback.userBanned();

//                    sendDebugResponse(user.getEmail(), url, "", response.toString(), user.getToken());

                    return;
                }

                if (isTokenDisabled(response)) {
                    if (callback != null)
                        callback.onTokenDisabled();

                    return;
                }

                if (response.has(Constants.RESPONSE_ERROR_KEY)) {
                    try {
                        if (callback != null)
                            callback.reportFail(getMessageForResponseCode(response.getString(Constants.RESPONSE_ERROR_KEY)));
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.reportFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                } else {
                    try {

                        if (response.has(report.getTicket())) {

                            Report reportResult = new Report();
                            JSONObject jsonObject = (JSONObject) response.get(report.getTicket());

                            reportResult.setReportId(jsonObject.getString("id_bache"));
                            reportResult.setDate(jsonObject.getString("bache_fecha_alta"));
                            reportResult.setTicket(jsonObject.getString("bache_ticket"));
                            reportResult.setEtapaId(jsonObject.getInt("id_cat_etapa"));
                            reportResult.setDelegationId(jsonObject.getInt("id_cat_delegacion"));
                            reportResult.setLongitude(jsonObject.getString("bache_longitud"));
                            reportResult.setLatitude(jsonObject.getString("bache_latitud"));
                            reportResult.setDescription(jsonObject.getString("bache_descripcion"));
                            reportResult.setColonia(jsonObject.getString("bache_colonia"));
                            reportResult.setAddress(jsonObject.getString("bache_direccion"));

                            if (jsonObject.has("id_cat_avenida")) {
                                reportResult.setAvenidaId(jsonObject.getInt("id_cat_avenida"));
                            }

                            if (jsonObject.has("bache_ticket_sales")) {
                                reportResult.setSalesForceTicket(jsonObject.getString("bache_ticket_sales"));
                            }

                            reportResult.setReportVersion(jsonObject.has("vialidad") ? jsonObject.getInt("vialidad") : Constants.NEW_VERSION);
                            report.setCause(jsonObject.has("razon_no_aplica") ? jsonObject.getString("razon_no_aplica") : "");

                            JSONObject jsonFotos = jsonObject.getJSONObject("fotos");

                            JSONArray images = jsonFotos.getJSONArray("imgs");

                            reportResult.setPictures(new ArrayList<Picture>());

                            if (images.length() > 0) {
                                for (int i = 0; i < images.length(); i++) {
                                    Picture reportPicture = new Picture();
                                    reportPicture.setEtapaId(images.getJSONObject(i).getInt("id_cat_etapa"));
                                    reportPicture.setFoto(images.getJSONObject(i).getString("foto"));

                                    reportResult.getPictures().add(reportPicture);
                                }
                            }

                            reportResult.setPushHistory(new ArrayList<PushRecord>());

                            try {
                                if (jsonObject.has("push_historial")) {
                                    JSONArray pushHistoryArray = jsonObject.getJSONArray("push_historial");
                                    for (int i = 0; i < pushHistoryArray.length(); i++) {
                                        PushRecord record = new PushRecord();

                                        record.setMessage(pushHistoryArray.getJSONObject(i).getString("push_mensaje"));
                                        record.setDate(pushHistoryArray.getJSONObject(i).getString("push_fecha_envio"));

                                        reportResult.getPushHistory().add(record);
                                    }
                                }
                            } catch (Exception ex) {

                            }

                            if (callback != null)
                                callback.reportSuccess(reportResult);
                        }

                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.reportFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (callback != null)
                    callback.reportFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                if (callback != null)
                    callback.reportFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.reportFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.reportFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.reportFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.reportFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.reportFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.reportFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.reportFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.reportFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.reportFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }
        });
    }

    public static void sendComments(final Comment comment, final CommentsCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(TIMEOUT);

        final String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_SEND_COMMENTS;

        RequestParams params = new RequestParams();

        client.addHeader("token", comment.getUser().getToken());

        params.add("comentario", comment.getMessage());
        params.add("bache_ticket", comment.getReport().getTicket());

        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if (isBannedUser(response)) {
                    if (callback != null)
                        callback.userBanned();

//                    sendDebugResponse(comment.getUser().getEmail(), url, "", response.toString(), comment.getUser().getToken());

                    return;
                }

                if (isTokenDisabled(response)) {
                    if (callback != null)
                        callback.onTokenDisabled();

                    return;
                }

                if (response.has(Constants.RESPONSE_ERROR_KEY)) {
                    try {
                        if (callback != null)
                            callback.onSendCommentsFail(getMessageForResponseCode(response.getString(Constants.RESPONSE_ERROR_KEY)));
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onSendCommentsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                } else {
                    if (callback != null)
                        callback.onSendCommentsSuccess();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                if (callback != null)
                    callback.onSendCommentsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                if (callback != null)
                    callback.onSendCommentsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onSendCommentsFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onSendCommentsFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onSendCommentsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onSendCommentsFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onSendCommentsFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onSendCommentsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onSendCommentsFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onSendCommentsFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onSendCommentsFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }
        });
    }

    public static void getRejectionList(User user, final RejectionsCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_GET_REJECTION_LIST;

        RequestParams params = new RequestParams();

        client.addHeader("token", user.getToken());

        client.post(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if (response.has(Constants.RESPONSE_ERROR_KEY)) {
                    try {
                        if (callback != null)
                            callback.onFail(getMessageForResponseCode(response.getString(Constants.RESPONSE_ERROR_KEY)));
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                } else {
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                try {

                    List<Rejection> rejectionList = new ArrayList<>();

                    if (response.length() > 0) {
                        for (int i = 0; i < response.length(); i++) {
                            Rejection rejection = new Rejection();

                            rejection.setId(response.getJSONObject(i).getInt("id_cat_motivos"));
                            rejection.setDescription(response.getJSONObject(i).getString("cat_motivo_rechazo_descripcion"));

                            rejectionList.add(rejection);
                        }
                    }

                    if (callback != null)
                        callback.onRejectionCallback(rejectionList);
                } catch (Exception ex) {
                    if (callback != null) {
                        callback.onFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

                if (callback != null)
                    callback.onFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }
        });
    }

    /// ESTE METODO ES EL ORIGINAL Y EL QUE FUNCIONA BIEN

    public static void sendAttendedReport(final Context context, final User currentUser, final AttendedReport attendedReport, final NewReportCallback callback) {
        final AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(TIMEOUT);

//        client.setMaxRetriesAndTimeout(1, TIMEOUT);

        String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_UPDATE_REPORT_BY_TROOP;

        RequestParams params = new RequestParams();

        String bachesString = attendedReport.arregloToJson();

        client.addHeader("token", attendedReport.getToken());

        params.put("bache_ticket", attendedReport.getTicket());
        params.put("numero_baches", attendedReport.getNoBaches());
        params.put("id_cat_etapa", attendedReport.getEtapa());
        params.put("arreglo_baches", bachesString);
        params.put("bache_img1", attendedReport.getImage1());
        params.put("bache_img2", attendedReport.getImage2());
        params.put("bache_img3", attendedReport.getImage3());
        params.put("bache_comentarios", attendedReport.getDescripcion());

        //QUITAR PARA PRODUCCION

        params.put("tramo", attendedReport.getStretch());
        params.put("sentido", attendedReport.getOrientation());
        params.put("lugarfisico", attendedReport.getPhysicalPlace());

        //QUITAR PARA PRODUCCION

        /*B24Debug debugObject = attendedReport.debugObject();
        debugObject.setReportSquadEmail(currentUser.getEmail());
        ServicesManager.sendDebugData(debugObject);*/

//        Log.i("BACHE_SERVICES", "sendAttendedReport executed for ticket = " + attendedReport.getTicket());
//        Utils.Log("ServicesManager", "sendAttendedReport", "Service call for " + attendedReport.getTicket());

        timerFinished = false;

        final CountDownTimer timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                client.cancelRequests(context, true);

                if (timerFinished) {
//                    Log.i("BACHE_TIMEOUT", "timer already finished");

                    return;
                }

                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "timer finished = " + new Date().toString());

                if (callback != null)
                    callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                return;
            }
        };

//        Log.i("BACHE_TIMEOUT", "timer started = " + new Date().toString());

        timer.start();

        client.post(context, url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                timer.cancel();
                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "success = " + new Date().toString());

//                Utils.Log("ServicesManager", "sendAttendedReport->onSuccess", "Service response " + response.toString());

                FileManager.writeLog(attendedReport.getTicket(), response != null ? response.toString() : "Response Null");

                if (isTokenDisabled(response)) {
                    if (callback != null)
                        callback.onTokenDisabled();

                    return;
                }

                if (response.has("OK")) {
                    try {
                        if (response.getString("OK").trim().equals("807")) {

                            if (callback != null)
                                callback.onFailRegisterReport(Constants.REPORTE_ATTENDED);

                        } else if (response.getString("OK").equals("805") ||
                            response.getString("OK").equals("806")) {
                            if (callback != null){
                                callback.onSuccessRegisterReport(null);
                            }
                        } else {
                            if (callback != null) {
                                callback.onFailRegisterReport(getMessageWithErrorCode(response.getString("OK")));
                            }
                        }

                    } catch (JSONException ex) {
                        if (callback != null) {
                            callback.onFailRegisterReport(getMessageWithErrorCode("006"));
                        }
                    }
                } else if (response.has(Constants.RESPONSE_ERROR_KEY)) {
                    try {
                        if (callback != null) {
                            callback.onFailRegisterReport(getMessageWithErrorCode(response.getString(Constants.RESPONSE_ERROR_KEY)));
                        }
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onFailRegisterReport(getMessageWithErrorCode("007"));
                    }
                } else {
                    if (callback != null)
                        callback.onFailRegisterReport(getMessageWithErrorCode("008"));
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;

                FileManager.writeLog(attendedReport.getTicket(), response != null ? response.toString() : "Response Null");
                if (callback != null)
                    callback.onFailRegisterReport(getMessageWithErrorCode("005"));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;

                FileManager.writeLog(attendedReport.getTicket(), responseString != null ? responseString : "Response Null");
                if (callback != null)
                    callback.onFailRegisterReport(getMessageWithErrorCode("004"));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

//                Utils.Log("ServicesManager", "sendAttendedReport->onFailure", "Service response " + errorResponse != null ? errorResponse.toString() : "");

                FileManager.writeLog(attendedReport.getTicket(), errorResponse != null ? errorResponse.toString() : "Response Null");

                /*String message = ServicesManager.getJSONStringWithParameters(attendedReport.getTicket(), currentUser.getEmail(), errorResponse != null ? errorResponse.toString() : "Response Null");
                ServicesManager.sendDebugThrowableData(attendedReport, throwable, message, statusCode + "");*/

                client.cancelRequests(context, true);

                /*if (throwable != null) {

                    if (throwable.getMessage() != null) {
                        if (throwable.getMessage().contains("timed out")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    } else {
                        if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

//                Utils.Log("ServicesManager", "sendAttendedReport->onFailure", "Service response " + responseString);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "Failure2 = " + new Date().toString());

                FileManager.writeLog(attendedReport.getTicket(), responseString != null ? responseString.toString() : "Response Null");

                /*String message = ServicesManager.getJSONStringWithParameters(attendedReport.getTicket(), currentUser.getEmail(), responseString != null ? responseString : "Response Null");
                ServicesManager.sendDebugThrowableData(attendedReport, throwable, message, statusCode + "");*/

                /*if (throwable != null) {

                    if (throwable.getMessage() != null) {
                        if (throwable.getMessage().contains("timed out")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    } else {
                        if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "Failure3 = " + new Date().toString());

//                Utils.Log("ServicesManager", "sendAttendedReport->onFailure", "Service response " + errorResponse.toString());

                FileManager.writeLog(attendedReport.getTicket(), errorResponse != null ? errorResponse.toString() : "Response Null");

                /*String message = ServicesManager.getJSONStringWithParameters(attendedReport.getTicket(), currentUser.getEmail(), errorResponse != null ? errorResponse.toString() : "Response Null");
                ServicesManager.sendDebugThrowableData(attendedReport, throwable, message, statusCode + "");*/

                /*if (throwable != null) {

                    if (throwable.getMessage() != null) {
                        if (throwable.getMessage().contains("timed out")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    } else {
                        if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }
        });
    }

    public static void sendWrongReport(final Context context, final User currentUser, final AttendedReport attendedReport, final NewReportCallback callback) {
        final AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(TIMEOUT);

//        client.setMaxRetriesAndTimeout(1, TIMEOUT);

        String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_UPDATE_REPORT_BY_TROOP;

        RequestParams params = new RequestParams();

        client.addHeader("token", attendedReport.getToken());

        params.put("bache_ticket", attendedReport.getTicket());
        params.put("numero_baches", attendedReport.getNoBaches());
        params.put("id_cat_etapa", attendedReport.getEtapa());
        params.put("bache_img1", attendedReport.getImage1());
        params.put("bache_img2", attendedReport.getImage2());
        params.put("bache_img3", attendedReport.getImage3());
        params.put("bache_descripcion", attendedReport.getDescripcion());
        params.put("razon_no_aplica", attendedReport.getCausa());

        /*B24Debug debugObject = attendedReport.debugObject();
        debugObject.setReportSquadEmail(currentUser.getEmail());
        ServicesManager.sendDebugData(debugObject);*/

//        Log.i("BACHE_SERVICES", "sendWrongReport executed for ticket = " + attendedReport.getTicket());

        timerFinished = false;

        final CountDownTimer timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                client.cancelRequests(context, true);

                if (timerFinished) {
//                    Log.i("BACHE_TIMEOUT", "timer already finished");

                    return;
                }

//                Log.i("BACHE_TIMEOUT", "timer finished = " + new Date().toString());

                if (callback != null)
                    callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                return;
            }
        };

//        Log.i("BACHE_TIMEOUT", "timer started = " + new Date().toString());

        timer.start();

        client.post(context, url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                timer.cancel();
                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "success = " + new Date().toString());

                FileManager.writeLog(attendedReport.getTicket(), response != null ? response.toString() : "Response Null");

                if (isTokenDisabled(response)) {
                    if (callback != null)
                        callback.onTokenDisabled();

                    return;
                }

                if (response.has("OK")) {
                    try {
                        if (response.getString("OK").trim().equals("807")) {

                            if (callback != null)
                                callback.onFailRegisterReport(Constants.REPORTE_ATTENDED);

                        } else if (response.getString("OK").equals("805") ||
                            response.getString("OK").trim().equals("806") ||
                            response.getString("OK").trim().equals("921") ||
                            response.getString("OK").trim().contains("921") ||
                            response.getString("OK").trim().contains("922")) {
                            if (callback != null){
                                callback.onSuccessRegisterReport(null);
                            }
                        } else {
                            if (callback != null) {
                                callback.onFailRegisterReport(getMessageWithErrorCode(response.getString("OK")));
                            }
                        }

                    } catch (JSONException ex) {
                        if (callback != null) {
                            callback.onFailRegisterReport(getMessageWithErrorCode("006"));
                        }
                    }
                } else if (response.has(Constants.RESPONSE_ERROR_KEY)) {
                    try {
                        if (callback != null) {
                            callback.onFailRegisterReport(getMessageWithErrorCode(response.getString(Constants.RESPONSE_ERROR_KEY)));
                        }
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onFailRegisterReport(getMessageWithErrorCode("007"));
                    }
                } else {
                    if (callback != null)
                        callback.onFailRegisterReport(getMessageWithErrorCode("008"));
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;

                FileManager.writeLog(attendedReport.getTicket(), response != null ? response.toString() : "Response Null");
                if (callback != null)
                    callback.onFailRegisterReport(getMessageWithErrorCode("005"));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;

                FileManager.writeLog(attendedReport.getTicket(), responseString != null ? responseString.toString() : "Response Null");

                if (callback != null)
                    callback.onFailRegisterReport(getMessageWithErrorCode("004"));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

                FileManager.writeLog(attendedReport.getTicket(), errorResponse != null ? errorResponse.toString() : "Response Null");

                /*String message = ServicesManager.getJSONStringWithParameters(attendedReport.getTicket(), currentUser.getEmail(), errorResponse != null ? errorResponse.toString() : "Response Null");
                ServicesManager.sendDebugThrowableData(attendedReport, throwable, message, statusCode + "");*/

                /*if (throwable != null) {

                    if (throwable.getMessage() != null) {
                        if (throwable.getMessage().contains("timed out")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    } else {
                        if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

                FileManager.writeLog(attendedReport.getTicket(), responseString != null ? responseString : "Response Null");

                /*String message = ServicesManager.getJSONStringWithParameters(attendedReport.getTicket(), currentUser.getEmail(), responseString != null ? responseString : "Response Null");
                ServicesManager.sendDebugThrowableData(attendedReport, throwable, message, statusCode + "");*/

                /*if (throwable != null) {

                    if (throwable.getMessage() != null) {
                        if (throwable.getMessage().contains("timed out")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    } else {
                        if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

                FileManager.writeLog(attendedReport.getTicket(), errorResponse != null ? errorResponse.toString() : "Response Null");

                /*String message = ServicesManager.getJSONStringWithParameters(attendedReport.getTicket(), currentUser.getEmail(), errorResponse != null ? errorResponse.toString() : "Response Null");
                ServicesManager.sendDebugThrowableData(attendedReport, throwable, message, statusCode + "");*/

                /*if (throwable != null) {

                    if (throwable.getMessage() != null) {
                        if (throwable.getMessage().contains("timed out")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    } else {
                        if (throwable != null) {
                            if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                                if (callback != null)
                                    callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                                return;
                            }
                        }
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }
        });
    }

    public static void getUserInfo(final User currentUser, final LoginCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(TIMEOUT);

//        client.setMaxRetriesAndTimeout(1, TIMEOUT);

        client.addHeader("token", currentUser.getToken());

        client.post(Constants.SERVICES_USER_INFO, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if (isBannedUser(response)) {
                    if (callback != null)
                        callback.userBanned();

//                    sendDebugResponse(currentUser.getEmail(), Constants.SERVICES_USER_INFO, "", response.toString(), currentUser.getToken());

                    return;
                }

                if (response.has(Constants.RESPONSE_ERROR_KEY)) {
                    try {
                        if (callback != null)
                            callback.loginFail(getMessageForResponseCode(response.getString(Constants.RESPONSE_ERROR_KEY)));
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.loginFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                } else {
                    try {

                        currentUser.setName(response.getString("nombre"));
                        currentUser.setFirtsLastName(response.getString("apellidoPa"));
                        currentUser.setSecondLastName(response.getString("apellidoMa"));
                        currentUser.setPhone(response.getString("telefono"));
                        currentUser.setEmail(response.getString("correo"));
                        currentUser.setUserType(response.getInt("tipo_usuario"));
                        currentUser.setPicture(response.getString("avatar"));
                        currentUser.setPictureUrl(response.getString("avatar"));
                        currentUser.setIdDelegacion(response.getInt("delegacion"));;

                        if (callback != null)
                            callback.loginSuccess(currentUser);
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.loginFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
                    }
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                callback.loginFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                callback.loginFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.loginFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.loginFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                callback.loginFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.loginFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.loginFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                callback.loginFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.loginFail(Constants.TIMEOUT_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.loginFail(Constants.TIMEOUT_DESCRIPTION);

                    return;
                }

                callback.loginFail(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }
        });
    }

    public static void sendSecondaryRoadReport(final Context context,
                                               final User currentUser,
                                               //final Report currentReport,
                                               final AttendedReport currentReport,
                                               final NewReportCallback callback) {
        final AsyncHttpClient client = new AsyncHttpClient();

//        client.setTimeout(TIMEOUT);

//        client.setMaxRetriesAndTimeout(1, TIMEOUT);

        String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_SECONDARY_ROAD;

        RequestParams params = new RequestParams();

        client.addHeader("token", currentUser.getToken());

        params.put("bache_ticket", currentReport.getTicket());
        params.put("bache_img1", currentReport.getImage1());

        /*B24Debug debugObject = currentReport.debugObject();
        debugObject.setReportSquadEmail(currentUser.getEmail());
        debugObject.setReportSquadToken(currentUser.getToken());

        ServicesManager.sendDebugData(debugObject);*/

        timerFinished = false;

        final CountDownTimer timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                client.cancelRequests(context, true);

                if (timerFinished) {
//                    Log.i("BACHE_TIMEOUT", "timer already finished");

                    return;
                }

//                Log.i("BACHE_TIMEOUT", "timer finished = " + new Date().toString());

                if (callback != null)
                    callback.onFailRegisterReport(Constants.TIMEOUT_DESCRIPTION);

                return;
            }
        };

//        Log.i("BACHE_TIMEOUT", "timer started = " + new Date().toString());

        timer.start();

        client.post(context, url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Success = " + new Date().toString());

                FileManager.writeLog(currentReport.getTicket(), response != null ? response.toString() : "Response Null");

                printResponse(response.toString());

                if (isTokenDisabled(response)) {
                    if (callback != null)
                        callback.onTokenDisabled();

                    return;
                }

                if (response.has("OK")) {
                    try {
                        if (response.getString("OK").trim().equals("807")) {

                            if (callback != null)
                                callback.onFailRegisterReport(Constants.REPORTE_ATTENDED);

                        } else if (response.getString("OK").equals("805") ||
                                response.getString("OK").trim().equals("806") ||
                                response.getString("OK").trim().equals("921") ||
                                response.getString("OK").trim().contains("921") ||
                                response.getString("OK").trim().contains("922")) {
                            if (callback != null){
                                callback.onSuccessRegisterReport(null);
                            }
                        } else {
                            if (callback != null) {
                                callback.onFailRegisterReport(getMessageWithErrorCode(response.getString("OK")));
                            }
                        }

                    } catch (JSONException ex) {
                        if (callback != null) {
                            callback.onFailRegisterReport(getMessageWithErrorCode("006"));
                        }
                    }
                } else if (response.has(Constants.RESPONSE_ERROR_KEY)) {
                    try {
                        if (callback != null) {
                            callback.onFailRegisterReport(getMessageWithErrorCode(response.getString(Constants.RESPONSE_ERROR_KEY)));
                        }
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onFailRegisterReport(getMessageWithErrorCode("007"));
                    }
                } else {
                    if (callback != null)
                        callback.onFailRegisterReport(getMessageWithErrorCode("008"));
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;

                FileManager.writeLog(currentReport.getTicket(), response != null ? response.toString() : "Response Null");
                if (callback != null)
                    callback.onFailRegisterReport(getMessageWithErrorCode("005"));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;

                FileManager.writeLog(currentReport.getTicket(), responseString != null ? responseString.toString() : "Response Null");
                if (callback != null)
                    callback.onFailRegisterReport(getMessageWithErrorCode("004"));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

                FileManager.writeLog(currentReport.getTicket(), errorResponse != null ? errorResponse.toString() : "Response Null");

                /*String message = ServicesManager.getJSONStringWithParameters(currentReport.getTicket(), currentUser.getEmail(), errorResponse != null ? errorResponse.toString() : "Response Null");
                ServicesManager.sendDebugThrowableDataWithToken(currentReport, throwable, message, statusCode + "", currentUser.getToken());*/

                /*if (throwable != null) {
                    if (throwable.getMessage() != null) {
                        if (throwable.getMessage().contains("timed out")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    } else {
                        if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(getMessageWithErrorCode("003"));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

                FileManager.writeLog(currentReport.getTicket(), responseString != null ? responseString : "Response Null");

                /*String message = ServicesManager.getJSONStringWithParameters(currentReport.getTicket(), currentUser.getEmail(), responseString != null ? responseString : "Response Null");
                ServicesManager.sendDebugThrowableDataWithToken(currentReport, throwable, message, statusCode + "", currentUser.getToken());*/

                /*if (throwable != null) {
                    if (throwable.getMessage() != null) {
                        if (throwable.getMessage().contains("timed out")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    } else {
                        if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(getMessageWithErrorCode("002"));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

                FileManager.writeLog(currentReport.getTicket(), errorResponse != null ? errorResponse.toString() : "Response Null");

                /*String message = ServicesManager.getJSONStringWithParameters(currentReport.getTicket(), currentUser.getEmail(), errorResponse != null ? errorResponse.toString() : "Response Null");
                ServicesManager.sendDebugThrowableDataWithToken(currentReport, throwable, message, statusCode + "", currentUser.getToken());*/

                /*if (throwable != null) {
                    if (throwable.getMessage() != null) {
                        if (throwable.getMessage().contains("timed out")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    } else {
                        if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(getMessageWithErrorCode("001"));
            }
        });
    }

    public static void cancelReportAttention(final Context context, final User currentUser, final Report currentReport, final NewReportCallback callback) {
        final AsyncHttpClient client = new AsyncHttpClient();
//        client.setTimeout(TIMEOUT);

        String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_CANCEL_REPORT_ATTENTION;

        RequestParams params = new RequestParams();

        client.addHeader("token", currentUser.getToken());

        params.put("bache_ticket", currentReport.getTicket());

        /*B24Debug debugObject = currentReport.debugObject();
        debugObject.setReportSquadEmail(currentUser.getEmail());
        debugObject.setReportSquadToken(currentUser.getToken());*/

//        ServicesManager.sendDebugData(debugObject);

//        Log.i("BACHE_SERVICES", "cancelReportAttention executed for ticket = " + currentReport.getTicket());
//        Utils.Log("ServicesManager", "cancelReportAttention", "Service call for = " + currentReport.getTicket());

        timerFinished = false;

        final CountDownTimer timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                client.cancelRequests(context, true);

                if (timerFinished) {
//                    Log.i("BACHE_TIMEOUT", "timer already finished");

                    return;
                }

//                Log.i("BACHE_TIMEOUT", "timer finished = " + new Date().toString());

                if (callback != null)
                    callback.onFailRegisterReport(Constants.TIMEOUT_DESCRIPTION);

                return;
            }
        };

//        Log.i("BACHE_TIMEOUT", "timer started = " + new Date().toString());

        timer.start();


        client.post(context, url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

//                Utils.Log("ServicesManager", "onSuccess", "Service response = " + response.toString());

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Success = " + new Date().toString());

                FileManager.writeLog(currentReport.getTicket(), response != null ? response.toString() : "Response Null");

                /*String message = ServicesManager.getJSONStringWithParameters(currentReport.getTicket(),
                        currentUser.getEmail(),
                        response != null ? response.toString() : "Response Null");
                ServicesManager.sendDebugThrowableDataWithToken(currentReport, null, message, statusCode + "", currentUser.getToken());*/

//                Log.i("VALID_REPORT_ACT_LOG", "[" + currentReport.getTicket() + "] Action = Request -> " + response.toString());

                if (isTokenDisabled(response)) {
                    if (callback != null)
                        callback.onTokenDisabled();

                    return;
                }

                if (response.has("OK")) {
                    try {
                        if (response.getString("OK").equals("805") ||
                                response.getString("OK").trim().equals("806") ||
                                response.getString("OK").trim().equals("807") ||
                                response.getString("OK").trim().equals("921") ||
                                response.getString("OK").trim().contains("921") ||
                                response.getString("OK").trim().contains("922") ||
                                response.getString("OK").trim().contains("801")) {
                            if (callback != null){
                                callback.onSuccessRegisterReport(null);
                            }
                        } else {
                            if (callback != null) {
                                callback.onFailRegisterReport(getMessageWithErrorCode(response.getString("OK")));
                            }
                        }

                    } catch (JSONException ex) {
                        if (callback != null) {
                            callback.onFailRegisterReport(getMessageWithErrorCode("006"));
                        }
                    }
                } else if (response.has(Constants.RESPONSE_ERROR_KEY)) {
                    try {
                        if (callback != null) {
                            callback.onFailRegisterReport(getMessageWithErrorCode(response.getString(Constants.RESPONSE_ERROR_KEY)));
                        }
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onFailRegisterReport(getMessageWithErrorCode("007"));
                    }
                } else {
                    if (callback != null)
                        callback.onFailRegisterReport(getMessageWithErrorCode("008"));
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                FileManager.writeLog(currentReport.getTicket(), response != null ? response.toString() : "Response Null");
                if (callback != null)
                    callback.onFailRegisterReport(getMessageWithErrorCode("005"));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                FileManager.writeLog(currentReport.getTicket(), responseString != null ? responseString.toString() : "Response Null");
                if (callback != null)
                    callback.onFailRegisterReport(getMessageWithErrorCode("004"));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

//                Utils.Log("ServicesManager", "onFailure", "Service response failure = " + errorResponse.toString());

                FileManager.writeLog(currentReport.getTicket(), errorResponse != null ? errorResponse.toString() : "Response Null");

                /*String message = ServicesManager.getJSONStringWithParameters(currentReport.getTicket(), currentUser.getEmail(), errorResponse != null ? errorResponse.toString() : "Response Null");
                ServicesManager.sendDebugThrowableDataWithToken(currentReport, throwable, message, statusCode + "", currentUser.getToken());*/

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);//getMessageWithErrorCode("003"));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

//                Utils.Log("ServicesManager", "onFailure", "Service response failure = " + responseString);

                FileManager.writeLog(currentReport.getTicket(), responseString != null ? responseString : "Response Null");

                /*String message = ServicesManager.getJSONStringWithParameters(currentReport.getTicket(), currentUser.getEmail(), responseString != null ? responseString : "Response Null");
                ServicesManager.sendDebugThrowableDataWithToken(currentReport, throwable, message, statusCode + "", currentUser.getToken());*/

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);//getMessageWithErrorCode("002"));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());

//                Utils.Log("ServicesManager", "onFailure", "Service response failure = " + errorResponse.toString());

                FileManager.writeLog(currentReport.getTicket(), errorResponse != null ? errorResponse.toString() : "Response Null");

                /*String message = ServicesManager.getJSONStringWithParameters(currentReport.getTicket(), currentUser.getEmail(), errorResponse != null ? errorResponse.toString() : "Response Null");
                ServicesManager.sendDebugThrowableDataWithToken(currentReport, throwable, message, statusCode + "", currentUser.getToken());*/

                /*if (throwable != null) {
                    if (throwable.getCause() instanceof ConnectTimeoutException) {
                        if (callback != null)
                            callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                        return;
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);//getMessageWithErrorCode("001"));
            }
        });
    }

    public static void getLastVersion(final IGetVersionCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(TIMEOUT);

        String url = Constants.SERVICES_GET_LAST_VERSION;

        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                if (response.has("ver")) {
                    if (callback != null) {
                        try {
                            callback.versionResponse(response.get("ver").toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.versionResponse("");
                        }
                    }
                } else {
                    callback.versionResponse("");
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                callback.versionResponse("");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                callback.versionResponse("");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                callback.versionResponse("");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                callback.versionResponse("");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                callback.versionResponse("");
            }
        });
    }

    // CONSULTA DE CATALOGOS
    public static void getStatesList(final IStatusResponse callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(TIMEOUT);

        String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_STATUS_LIST;

        client.get(url, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                callback.onResponse(null);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);//AQUI

                List<Status> statusList = null;

                if (response != null) {

                    statusList = new ArrayList<>();

                    try {

                        for (int i = 0; i < response.length(); i++) {
                            Status status = new Status();
                            status.setId(response.getJSONObject(i).getInt("id_cat_etapa"));
                            status.setName(response.getJSONObject(i).getString("cat_etapa_descripcion"));

                            statusList.add(status);
                        }

                    } catch (JSONException ex) {

                    }
                }

                if (callback != null) {
                    callback.onResponse(statusList);
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
                callback.onResponse(null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                callback.onResponse(null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                callback.onResponse(null);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                callback.onResponse(null);
            }
        });
    }

    // NUEVOS ESTADOS 7 y 8
    public static void updateReportStatusAttention(final Context context, final User currentUser, final AttendedReport attendedReport, final NewReportCallback callback) {

        final AsyncHttpClient client = new AsyncHttpClient();

        client.setTimeout(TIMEOUT);

//        client.setMaxRetriesAndTimeout(1, TIMEOUT);

        String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_UPDATE_REPORT_BY_TROOP;

        RequestParams params = new RequestParams();

        client.addHeader("token", attendedReport.getToken());
        params.put("bache_ticket", attendedReport.getTicket());
        params.put("id_cat_etapa", attendedReport.getEtapa());
        params.put("bache_img1", attendedReport.getImage1());

        if (attendedReport.getEtapa() == Constants.REPORT_STATUS_7) {
            params.put("razon_no_aplica", attendedReport.getBacheJustificacion());
            params.put("bache_compromiso", attendedReport.getBacheCompromiso());
        } else if (attendedReport.getEtapa() == Constants.REPORT_STATUS_8) {
            params.put("bache_reasignacion", attendedReport.getBacheReasignacion());
            params.put("bache_comentarios", attendedReport.getDescripcion());
        }

        /*B24Debug debugObject = attendedReport.debugObject();
        debugObject.setReportSquadEmail(currentUser.getEmail());
        ServicesManager.sendDebugData(debugObject);*/

        timerFinished = false;

        final CountDownTimer timer = new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {

                client.cancelRequests(context, true);

                if (timerFinished) {
//                    Log.i("BACHE_TIMEOUT", "timer already finished");

                    return;
                }

//                Log.i("BACHE_TIMEOUT", "timer finished = " + new Date().toString());

                if (callback != null)
                    callback.onFailRegisterReport(Constants.TIMEOUT_DESCRIPTION);

                return;
            }
        };

//        Log.i("BACHE_TIMEOUT", "timer started = " + new Date().toString());

        timer.start();

        client.post(context, url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);

                timer.cancel();
                timerFinished = true;

//                Log.i("BACHE_TIMEOUT", "success = " + new Date().toString());

                FileManager.writeLog(attendedReport.getTicket(), response != null ? response.toString() : "Response Null");

                if (isTokenDisabled(response)) {
                    if (callback != null)
                        callback.onTokenDisabled();

                    return;
                }

                if (response.has("OK")) {
                    try {
                        if (response.getString("OK").trim().equals("807")) {

                            if (callback != null)
                                callback.onFailRegisterReport(Constants.REPORTE_ATTENDED);

                        } else if (response.getString("OK").equals("805") ||
                                response.getString("OK").trim().equals("806") ||
                                response.getString("OK").trim().equals("921") ||
                                response.getString("OK").trim().contains("921") ||
                                response.getString("OK").trim().contains("922") ||
                                response.getString("OK").trim().equals("802")) {
                            if (callback != null){
                                callback.onSuccessRegisterReport(null);
                            }
                        } else {
                            if (callback != null) {
                                callback.onFailRegisterReport(getMessageWithErrorCode(response.getString("OK")));
                            }
                        }

                    } catch (JSONException ex) {
                        if (callback != null) {
                            callback.onFailRegisterReport(getMessageWithErrorCode("006"));
                        }
                    }
                } else if (response.has(Constants.RESPONSE_ERROR_KEY)) {
                    try {
                        if (callback != null) {
                            callback.onFailRegisterReport(getMessageWithErrorCode(response.getString(Constants.RESPONSE_ERROR_KEY)));
                        }
                    } catch (JSONException ex) {
                        if (callback != null)
                            callback.onFailRegisterReport(getMessageWithErrorCode("007"));
                    }
                } else {
                    if (callback != null)
                        callback.onFailRegisterReport(getMessageWithErrorCode("008"));
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                FileManager.writeLog(attendedReport.getTicket(), response != null ? response.toString() : "Response Null");
                if (callback != null)
                    callback.onFailRegisterReport(getMessageWithErrorCode("005"));
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                FileManager.writeLog(attendedReport.getTicket(), responseString != null ? responseString.toString() : "Response Null");

                if (callback != null)
                    callback.onFailRegisterReport(getMessageWithErrorCode("004"));
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

//                Log.i("ERROR_REUBICAR", errorResponse.toString());

                FileManager.writeLog(attendedReport.getTicket(), errorResponse != null ? errorResponse.toString() : "Response Null");

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());



                /*String message = ServicesManager.getJSONStringWithParameters(attendedReport.getTicket(), currentUser.getEmail(), errorResponse != null ? errorResponse.toString() : "Response Null");
                ServicesManager.sendDebugThrowableData(attendedReport, throwable, message, statusCode + "");*/

                /*if (throwable != null) {

                    if (throwable.getMessage() != null) {
                        if (throwable.getMessage().contains("timed out")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    } else {
                        if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);

//                Log.i("ERROR_REUBICAR", responseString.toString());
                FileManager.writeLog(attendedReport.getTicket(), responseString != null ? responseString : "Response Null");

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());



                /*String message = ServicesManager.getJSONStringWithParameters(attendedReport.getTicket(), currentUser.getEmail(), responseString != null ? responseString : "Response Null");
                ServicesManager.sendDebugThrowableData(attendedReport, throwable, message, statusCode + "");*/

                /*if (throwable != null) {

                    if (throwable.getMessage() != null) {
                        if (throwable.getMessage().contains("timed out")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    } else {
                        if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

//                Log.i("ERROR_REUBICAR", errorResponse.toString());
                FileManager.writeLog(attendedReport.getTicket(), errorResponse != null ? errorResponse.toString() : "Response Null");

                if (timerFinished) {
                    client.cancelRequests(context, true);

                    return;
                }

                timer.cancel();
                timerFinished = true;
//                Log.i("BACHE_TIMEOUT", "Failure1 = " + new Date().toString());



                /*String message = ServicesManager.getJSONStringWithParameters(attendedReport.getTicket(), currentUser.getEmail(), errorResponse != null ? errorResponse.toString() : "Response Null");
                ServicesManager.sendDebugThrowableData(attendedReport, throwable, message, statusCode + "");*/

                /*if (throwable != null) {

                    if (throwable.getMessage() != null) {
                        if (throwable.getMessage().contains("timed out")) {
                            if (callback != null)
                                callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                            return;
                        }
                    } else {
                        if (throwable != null) {
                            if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                                if (callback != null)
                                    callback.onFailRegisterReport(Constants.AGU_DESCRIPTION);

                                return;
                            }
                        }
                    }
                }*/

                if (isTimeOutException(throwable)) {
                    if (callback != null)
                        callback.onFailRegisterReport(Constants.TIMEOUT_REPORT_ATTENTION_DESCRIPTION);

                    return;
                }

                if (callback != null)
                    callback.onFailRegisterReport(Constants.ERROR_DATABASE_ERROR_DESCRIPTION);
            }
        });
    }

    public static void sendFirebaseToken(final Context context, User currentUser, String firebaseToken) {
        AsyncHttpClient client = new AsyncHttpClient();

        String url = Constants.SERVICES_SERVER_URL + Constants.SERVICES_SEND_FIREBASE_TOKEN;

        RequestParams params = new RequestParams();

        client.addHeader("token", currentUser.getToken());
        params.put("correo", currentUser.getEmail());
        params.put("tokenD", firebaseToken);

        client.post(context, url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("FirebaseServer", "token save successfully");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

                if (throwable != null) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    private static Report.ReportOrigin getOrigin(int parameter) {
        Report.ReportOrigin origin;
        switch (parameter) {
            case Constants.REPORT_ORIGIN_APP:
                origin = Report.ReportOrigin.APP;
                break;
            case Constants.REPORT_ORIGIN_CMS:
                origin = Report.ReportOrigin.CMS;
                break;
            case Constants.REPORT_ORIGIN_072_SALESFORCE:
                origin = Report.ReportOrigin.O72;
                break;
            case Constants.REPORT_ORIGIN_SMS:
                origin = Report.ReportOrigin.SMS;
                break;
            default:
                origin = Report.ReportOrigin.APP;
                break;
        }

        return origin;
    }

    private static boolean isBannedUser(JSONObject object) {
        try {
            if (object.has("ERROR")) {
                int responseCode = Integer.valueOf(object.getString("ERROR"));
                if (responseCode == Constants.ERROR_USER_BANNED) {
                    return true;
                }
            }

            if (object.has("OK")) {
                int responseCode = Integer.valueOf(object.getString("OK"));
                if (responseCode == Constants.ERROR_USER_BANNED) {
                    return true;
                }
            }
        } catch (Exception ex) {
            return false;
        }

        return false;
    }

    private static boolean isTokenDisabled(JSONObject object) {
        try {
            if (object.has("ERROR")) {
                int responseCode = Integer.valueOf(object.getString("ERROR"));
                if (responseCode == Constants.ERROR_INCORRECT_TOKEN) {
                        //|| responseCode == Constants.ERROR_BAD_TOKEN) {
                    return true;
                }
            }
        } catch (Exception ex) {

        }

        return false;
    }

    private static String getMessageForResponseCode(String responseCodeString) {
        String responseMessage = "";
        int responseCode = Integer.valueOf(responseCodeString);

        switch (responseCode) {
            case Constants.ERROR_MISSING_DATA:
                responseMessage = Constants.ERROR_MISSING_DATA_DESCRIPTION;
                break;
            case Constants.ERROR_INCORRECT_DATA:
                responseMessage = Constants.ERROR_INCORRECT_DATA_DESCRIPTION;
                break;
            case Constants.ERROR_DATABASE_ERROR:
                responseMessage = Constants.ERROR_DATABASE_ERROR_DESCRIPTION;
                break;
            case Constants.ERROR_INCORRECT_TOKEN:
                responseMessage = "";
                break;
            case Constants.ERROR_EMAIL_ERROR:
                responseMessage = Constants.ERROR_EMAIL_ERROR_DESCRIPTION;
                break;
            case Constants.ERROR_BAD_COORDINATES:
                responseMessage = Constants.ERROR_BAD_COORDINATES_DESCRIPTION;
                break;
//            case Constants.ERROR_BAD_TOKEN:
//                responseMessage = "";
//                break;
            case Constants.ERROR_NO_REPORTS_FOUND:
                responseMessage = Constants.ERROR_NO_REPORTS_FOUND_DESCRIPTION;
                break;
            case Constants.ERROR_USER_PERMISION_DENIED:
                responseMessage = Constants.ERROR_USER_PERMISION_DENIED_DESCRIPTION;
                break;
            case Constants.ERROR_BAD_REPORT_ID:
                responseMessage = Constants.ERROR_BAD_REPORT_ID_DESCRIPTION;
                break;
            case Constants.ERROR_EMPTY_REPORT_ID:
                responseMessage = Constants.ERROR_EMPTY_REPORT_ID_DESCRIPTION;
                break;
            case Constants.ERROR_USER_ALREADY_REGISTERED:
                responseMessage = Constants.ERROR_USER_ALREADY_REGISTERED_DESCRIPTION;
                break;
            case Constants.ERROR_HASH_EMPTY:
                responseMessage = Constants.ERROR_HASH_EMPTY_DESCRIPTION;
                break;
            case Constants.ERROR_REPORT_ASSIGNATION_FAILED:
                responseMessage = Constants.ERROR_REPORT_ASSIGNATION_FAILED_DESCRIPTION;
                break;
            case Constants.ERROR_TROOP_STATUS:
                responseMessage = Constants.ERROR_TROOP_STATUS_DESCRIPTION;
                break;
            case Constants.ERROR_UPDATE_REPORT:
                responseMessage = Constants.ERROR_UPDATE_REPORT_DESCRIPTION;
                break;
            case Constants.ERROR_REPORT_EMPTY:
                responseMessage = Constants.ERROR_REPORT_EMPTY_DESCRIPTION;
                break;
            case Constants.ERROR_INVALID_USER:
                responseMessage = Constants.ERROR_INVALID_USER_DESCRIPTION;
                break;
//            case Constants.ERROR_REGISTER_INVALID:
//                responseMessage = Constants.ERROR_REGISTER_INVALID_DESCRIPTION;
//                break;
//            case Constants.ERROR_SOCIAL_NETWORK_EMPTY:
//                responseMessage = Constants.ERROR_SOCIAL_NETWORK_EMPTY_DESCRIPTION;
//                break;
//            case Constants.ERROR_PASSWORD_UPDATE:
//                responseMessage = Constants.ERROR_PASSWORD_UPDATE_DESCRIPTION;
//                break;
            case Constants.ERROR_EMAIL_ERROR_2:
                responseMessage = Constants.ERROR_EMAIL_ERROR_2_DESCRIPTION;
                break;
            case Constants.ERROR_USER_REGISTER:
                responseMessage = Constants.ERROR_USER_REGISTER_DESCRIPTION;
                break;
            case Constants.ERROR_ACCOUNT_NOT_ACTIVE:
                responseMessage = Constants.ERROR_ACCOUNT_NOT_ACTIVE_DESCRIPTION;
                break;
            case Constants.ERROR_USER_NOT_REGISTERED:
                responseMessage = Constants.ERROR_USER_NOT_REGISTERED_DESCRIPTION;
            default:
                responseMessage = Constants.ERROR_DATABASE_ERROR_DESCRIPTION;
                break;
        }

        return responseMessage;
    }

    public static String getMessageWithErrorCode(String responseCode) {

        return Constants.ERROR_GENERIC_PART_1 + " " + responseCode + Constants.ERROR_GENERIC_PART_2;
    }

    /*public static void sendDebugData(final B24Debug debugObject) {
        debugObject.saveEventually();
    }*/

    /*public static void sendDebugThrowableData(B24DebugInterface report, Throwable error, String errorDescription, String errorCode) {
        ServicesManager.sendDebugThrowableDataWithToken(report, error, errorDescription, errorCode, null);
    }*/

    /*public static void sendDebugThrowableDataWithToken(B24DebugInterface report, Throwable error, String errorDescription, String errorCode, String token) {

        B24DebugThrowable debugObject = new B24DebugThrowable();

        debugObject.setErrorDescription(errorDescription);
        debugObject.setErrorMessage(error != null ? error.getMessage() : "");
        debugObject.setReportID(report.getReportID());
        debugObject.setReportSquadToken(token != null ? token : report.getReportToken());

        debugObject.saveEventually();

        FlurryAgent.onError(errorCode, errorDescription, error);

    }*/

    public static String getJSONStringWithParameters(String reportID, String email, String errorMessage) {
        Map <String, String> parameters = new HashMap<String, String>();

        parameters.put("ReportID", reportID);
        parameters.put("SquadEmail", email);
        parameters.put("Message", errorMessage);

        Gson json = new Gson();
        return json.toJson(parameters);
    }

    /*public static void sendDebugResponse(String email, String serviceEndPoint, String request, String response, String token) {
        B24DebugResponse debugObject = new B24DebugResponse();

        debugObject.setEmail(email);
        debugObject.setServiceEndPoint(serviceEndPoint);
//        debugObject.setRequest(request);
        debugObject.setResponse(response);
        debugObject.setUserToken(token);

        Date currentDate = new Date();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        debugObject.setDate(dateFormat.format(currentDate));

        debugObject.saveEventually();
    }*/

    public static void printResponse(String response) {
//        Log.i("SERVICE_RESPONSE", response);
    }

    private static boolean isTimeOutException(Throwable throwable) {
        if (throwable != null) {

            if (throwable.getMessage() != null) {
                if (throwable.getMessage().contains("timed out")) {

                    return true;
                }
            } else {
                if (throwable.toString().equals("java.net.SocketTimeoutException")) {
                    return true;
                }
            }
        }

        return false;
    }
}
