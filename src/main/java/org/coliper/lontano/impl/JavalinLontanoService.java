package org.coliper.lontano.impl;

import static java.util.Objects.requireNonNull;

import org.coliper.lontano.AbstractLontanoService;
import org.coliper.lontano.RemoteInterfaceName;
import org.coliper.lontano.RemoteOperationName;
import org.coliper.lontano.ReturnValueWrapper;

import com.google.common.base.Preconditions;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.plugin.json.JavalinJson;

public class JavalinLontanoService extends AbstractLontanoService<JavalinLontanoService> {
    private static final String URI_PATH_SEPARATOR = "/";
    private static final String DEFAULT_ROOT_PATH = "/";
    private static final String PATH_PARAMETER_MARKER = ":";
    private static final String PATH_PARAMETER_INTERFACE = "interface";
    private static final String PATH_PARAMETER_OPERATION = "operation";
    private static final String DEFAULT_WRAPPER_PATH = "client.js";
    private static final String JAVASCRIPT_CONTENT_TYPE = "text/javascript; charset=UTF-8";

    private String rootPath = DEFAULT_ROOT_PATH;
    private String wrapperPath = DEFAULT_WRAPPER_PATH;

    public JavalinLontanoService() {
    }

    @Override
    protected Object deserializeFromJson(String json, Class<?> expectedType) {
        return JavalinJson.fromJson(json, expectedType);
    }

    public JavalinLontanoService rootPath(String rootPath) {
        requireNonNull(rootPath, "rootPath");
        Preconditions.checkArgument(rootPath.endsWith(URI_PATH_SEPARATOR),
                "root path '%s' does not end with a %s", rootPath, URI_PATH_SEPARATOR);
        this.rootPath = rootPath;
        return this;
    }

    public JavalinLontanoService wrapperPath(String wrapperPath) {
        this.wrapperPath = requireNonNull(wrapperPath, "wrapperPath");
        return this;
    }

    public void registerWithJavalin(Javalin javalin) {
        requireNonNull(javalin, "javalin");
        final String operationCallPath =
                this.rootPath + PATH_PARAMETER_MARKER + PATH_PARAMETER_INTERFACE
                        + URI_PATH_SEPARATOR + PATH_PARAMETER_MARKER + PATH_PARAMETER_OPERATION;
        javalin.post(operationCallPath, this::handleOperationCall);
        final String wrapperPath = this.rootPath + this.wrapperPath;
        javalin.get(wrapperPath, this::handleWrapperCall);
    }

    public void handleOperationCall(Context ctx) throws Exception {
        requireNonNull(ctx, "ctx");
        ReturnValueWrapper result = this.handleRequest(getInterfaceNameFromContext(ctx),
                getOperationNameFromContext(ctx), ctx.body());
        ctx.json(result);
    }

    public void handleWrapperCall(Context ctx) throws Exception {
        requireNonNull(ctx, "ctx");
        ctx.result("bla bla");
        ctx.contentType(JAVASCRIPT_CONTENT_TYPE);
    }

    private RemoteOperationName getOperationNameFromContext(Context ctx) {
        final String opNameString = ctx.pathParam(PATH_PARAMETER_OPERATION);
        Preconditions.checkState(opNameString != null, "missing operation name in path %s",
                ctx.contextPath());
        final RemoteOperationName opName = new RemoteOperationName(opNameString);
        return opName;
    }

    private RemoteInterfaceName getInterfaceNameFromContext(Context ctx) {
        final String ifNameString = ctx.pathParam(PATH_PARAMETER_INTERFACE);
        Preconditions.checkState(ifNameString != null, "missing interface name in path %s",
                ctx.contextPath());
        final RemoteInterfaceName ifName = new RemoteInterfaceName(ifNameString);
        return ifName;
    }

}
