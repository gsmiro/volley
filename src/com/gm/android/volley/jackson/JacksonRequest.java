package com.gm.android.volley.jackson;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by gsmiro on 30/05/2014.
 */
public class JacksonRequest<T> extends JsonRequest<List<T>> {

    private Class<T> clazz;

    private static String request(Object body) throws ParseError {
        try {
            return JSON.std.asString(body);
        } catch (IOException e) {
            throw new ParseError(e);
        }
    }

    public JacksonRequest(Class<T> responseType, int method, String url, Object requestBody, Response.Listener<List<T>> listener, Response.ErrorListener errorListener) throws ParseError {
        super(method, url, request(requestBody), listener, errorListener);
        this.clazz = responseType;
    }

    @Override
    protected Response<List<T>> parseNetworkResponse(NetworkResponse response) {
        try {
            List<T> ret;
            if (Iterable.class.isAssignableFrom(clazz)) {
                ret = JSON.std.listOfFrom(clazz, response.data);
            } else {
                ret = new LinkedList<T>();
                ret.add(JSON.std.beanFrom(clazz, response.data));
            }
            return Response.success(ret,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONObjectException e) {
            return Response.error(new ParseError(e));
        } catch (IOException e) {
            return Response.error(new ParseError(e));
        }
    }
}
