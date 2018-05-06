package com.cmi.bache24.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by omar on 11/22/15.
 */
public class Constants {

    public static final int SPLASH_TIME_IN_MILLISECONDS = 3000;

    public static final String TWITTER_KEY = "ZVtDlEzCyneeSymX8Vwsdc1jU";
    public static final String TWITTER_SECRET = "ANP9l6dRsShsu1WuYjk1q0SpX0C18gDtnlJIe2Raz8EEhS8byM";

    public static final String FACEBOOK_SOCIAL_NETWORK = "FACEBOOK-24";
    public static final String TWITTER_SOCIAL_NETWORK = "TWITTER-24";

    public static final String REGISTER_FACEBOOK = "REGISTER_FACEBOOK_";
    public static final String REGISTER_TWITTER = "REGISTER_TWITTER_";
    public static final String REGISTER_EMAIL = "REGISTER_EMAIL_";

    public static final String EXTRA_LOGIN_FROM_SOCIAL_NETWORK = "_EXTRA_LOGIN_FROM_SOCIAL_NETWORK";
    public static final String EXTRA_REGISTER_USER_DATA = "_EXTRA_REGISTER_USER_DATA";

    public static final String EXTRA_NEW_REPORT_LATITUDE = "EXTRA_NEW_REPORT_LATITUDE_";
    public static final String EXTRA_NEW_REPORT_LONGITUDE = "EXTRA_NEW_REPORT_LONGITUDE";
    public static final String EXTRA_NEW_REPORT_ADDRESS_AS_STRING = "EXTRA_NEW_REPORT_ADDRESS_AS_STRING";
    public static final String EXTRA_NEW_REPORT_IS_TEMPORARY = "EXTRA_NEW_REPORT_IS_TEMPORARY";
    public static final String EXTRA_NEW_REPORT_POSITION_IN_LIST = "EXTRA_NEW_REPORT_POSITION_IN_LIST";
    public static final String EXTRA_REPORT_STATUS_DETAIL = "_EXTRA_REPORT_STATUS_DETAIL";
    public static final String EXTRA_SHOULD_SHOW_ACTIVATION_MESSAGE = "_EXTRA_SHOULD_SHOW_ACTIVATION_MESSAGE";
    public static final String EXTRA_IMAGE_URL = "_EXTRA_IMAGE_URL";

    public static final String SERVICES_SERVER_URL = "https://bache24.agucdmx.gob.mx/api/v1.0"; // PRODUCCIÓN
    public static final String SERVICES_GET_LAST_VERSION = "https://bache24.agucdmx.gob.mx/info/v1.0/versionMovil"; // PRODUCCIÓN
    //public static final String SERVICES_SERVER_URL = "http://pruebasbache24.agucdmx.gob.mx/api/v1.0"; // DESARROLLO
    //public static final String SERVICES_GET_LAST_VERSION = "http://pruebasbache24.agucdmx.gob.mx/info/v1.0/versionMovil"; // DESARROLLO


    public static final String SERVICES_REGISTER = "/registroMovil";
    public static final String SERVICES_RECOVER_PASSWORD = "/recuperaPassMovil";
    public static final String SERVICES_LOGIN = "/accesoMovil";
    public static final String SERVICES_REGISTER_REPORT = "/altaReporte";
    public static final String SERVICES_UPDATE_USER = "/actulizaMovil";
    public static final String SERVICES_GET_REPORTS = "/muestraReportes";
    public static final String SERVICES_GET_TROOPS_REPORTS = "/muestraReportesTecnico";
    public static final String SERVICES_UPDATE_REPORT_BY_TROOP = "/atiendeBache";
    public static final String SERVICES_GET_REPORT_DETAIL = "/detalleReportePush";
    public static final String SERVICES_SEND_COMMENTS = "/enviaComentarios";
    public static final String SERVICES_GET_REJECTION_LIST = "/listaMotivosRechazo";
    public static final String SERVICES_USER_INFO = "https://bache24.mx/info/v1.0/usuario";
    public static final String SERVICES_SECONDARY_ROAD = "/bacheNoAplica";
    public static final String SERVICES_CANCEL_REPORT_ATTENTION = "/regresaBache";
    public static final String SERVICES_STATUS_LIST = "/listaEtapas";
    public static final String SERVICES_SEND_FIREBASE_TOKEN = "/guardaToken";

    public static final int REPORT_DETAIL_REQUEST_CODE = 6453;
    public static final String BUNDLE_REGISTER_REPORT_RESULT = "BUNDLE_REGISTER_REPORT_RESULT";
    public static final String BUNDLE_INFORMATION_ACTIVITY_TITLE = "BUNDLE_INFORMATION_ACTIVITY_TITLE";

    public static final int RESPONSE_OK_CODE = 200;

    public static final int ERROR_MISSING_DATA = 701;
    public static final int ERROR_INCORRECT_DATA = 702;
    public static final int ERROR_DATABASE_ERROR = 703;
    public static final int ERROR_INCORRECT_TOKEN = 704;
    public static final int ERROR_EMAIL_ERROR = 705;
    public static final int ERROR_BAD_COORDINATES = 706;

    //    public static final int ERROR_BAD_TOKEN = 707;
    public static final int ERROR_NO_REPORTS_FOUND = 715;
    public static final int ERROR_USER_PERMISION_DENIED = 716;
    public static final int ERROR_BAD_REPORT_ID = 718;
    public static final int ERROR_EMPTY_REPORT_ID = 719;
    public static final int ERROR_USER_ALREADY_REGISTERED = 720;
    public static final int ERROR_HASH_EMPTY = 721;
    public static final int ERROR_REPORT_ASSIGNATION_FAILED = 732;
    public static final int ERROR_TROOP_STATUS = 734;
    public static final int ERROR_UPDATE_REPORT = 735;
    public static final int ERROR_REPORT_EMPTY = 736;
    public static final int ERROR_INVALID_USER = 737;
    //    public static final int ERROR_REGISTER_INVALID = 738;
//    public static final int ERROR_SOCIAL_NETWORK_EMPTY = 739;
//    public static final int ERROR_PASSWORD_UPDATE = 740;
    public static final int ERROR_EMAIL_ERROR_2 = 741;
    public static final int ERROR_USER_REGISTER = 742;
    public static final int ERROR_USER_BANNED = 743;
    public static final int ERROR_UNDEFINED_PASS = 744;
    public static final int ERROR_BAD_REPORT_STATUS = 745;
    public static final int ERROR_INFO_NOT_FOUND = 746;
    public static final int ERROR_CANNOT_CHANGE_PASS = 747;
    public static final int ERROR_CANNOT_ADD_PUSH_HISTORY = 748;
    public static final int ERROR_ACCOUNT_NOT_ACTIVE = 749;
    public static final int ERROR_USER_NOT_REGISTERED = 752;


    public static final String ERROR_MISSING_DATA_DESCRIPTION = "Mmm... Parece que faltaron datos";
    public static final String ERROR_INCORRECT_DATA_DESCRIPTION = "Combinación de usuario/contraseña inválida. Por favor, verifica e inténtalo de nuevo.";
    public static final String ERROR_DATABASE_ERROR_DESCRIPTION = "Hubo un error al procesar tu solicitud. Por favor, inténtalo más tarde";
    public static final String ERROR_INCORRECT_TOKEN_DESCRIPTION = "Por favor vuelve a iniciar sesión";
    public static final String ERROR_EMAIL_ERROR_DESCRIPTION = "Hubo un error al enviar el correo. Por favor, inténtalo otra vez";
    public static final String ERROR_BAD_COORDINATES_DESCRIPTION = "Las coordenadas que enviaste no son válidas";

    public static final String ERROR_BAD_TOKEN_DESCRIPTION = "Por favor vuelve a iniciar sesión";
    public static final String ERROR_NO_REPORTS_FOUND_DESCRIPTION = "No has enviado reportes";
    public static final String ERROR_NO_REPORTS_FOUND_DESCRIPTION_2 = "No tienes reportes asignados";
    public static final String ERROR_USER_PERMISION_DENIED_DESCRIPTION = "Error inesperado, inténtalo más tarde";
    public static final String ERROR_BAD_REPORT_ID_DESCRIPTION = "Error inesperado, inténtalo más tarde";
    public static final String ERROR_EMPTY_REPORT_ID_DESCRIPTION = "Error inesperado, inténtalo más tarde";
    public static final String ERROR_USER_ALREADY_REGISTERED_DESCRIPTION = "Error inesperado, inténtalo más tarde";
    public static final String ERROR_HASH_EMPTY_DESCRIPTION = "Error inesperado, inténtalo más tarde";
    public static final String ERROR_REPORT_ASSIGNATION_FAILED_DESCRIPTION = "Error inesperado, inténtalo más tarde";
    public static final String ERROR_TROOP_STATUS_DESCRIPTION = "Error inesperado, inténtalo más tarde";
    public static final String ERROR_UPDATE_REPORT_DESCRIPTION = "No se pudo enviar el reporte, inténtalo más tarde";
    public static final String ERROR_REPORT_EMPTY_DESCRIPTION = "Parece que faltan datos del reporte";
    public static final String ERROR_INVALID_USER_DESCRIPTION = "Error inesperado, inténtalo más tarde";
    public static final String ERROR_REGISTER_INVALID_DESCRIPTION = "Error inesperado, inténtalo más tarde";
    public static final String ERROR_SOCIAL_NETWORK_EMPTY_DESCRIPTION = "Error inesperado, inténtalo más tarde";
    public static final String ERROR_PASSWORD_UPDATE_DESCRIPTION = "Error inesperado, inténtalo más tarde";
    public static final String ERROR_EMAIL_ERROR_2_DESCRIPTION = "Hubo un error al enviar el correo. Por favor, inténtalo otra vez";
    public static final String ERROR_USER_REGISTER_DESCRIPTION = "Error inesperado, inténtalo más tarde";
    public static final String ERROR_ACCOUNT_NOT_ACTIVE_DESCRIPTION = "Cuenta no activada. Revisa tu correo y sigue las instrucciones.";
    public static final String ERROR_USER_BANNED_DESCRIPTION = "El equipo de Bache 24 ha atendido e identificado como falsos 5 reportes, por lo que tu cuenta ha sido bloqueada permanentemente.";

    public static final String ERROR_UNDEFINED_PASS_DESCRIPTION = "Contraseña no definida";
    public static final String ERROR_BAD_REPORT_STATUS_DESCRIPTION = "Etapa incorrecta para realizar el envió de un push notification";
    public static final String ERROR_INFO_NOT_FOUND_DESCRIPTION = "No se mando información";
    public static final String ERROR_CANNOT_CHANGE_PASS_DESCRIPTION = "No se puede cambiar la contraseña no existe una registrada";
    public static final String ERROR_CANNOT_ADD_PUSH_HISTORY_DESCRIPTION = "No se logro insertar el histórico del push notification";

    public static final String ERROR_USER_NOT_REGISTERED_DESCRIPTION = "El correo que ingresaste no existe en nuestro sistema, te invitamos a registrarte o prueba con otra cuenta.";

    public static final String TIMEOUT_DESCRIPTION = "Error de conexión, intenta de nuevo.";
    public static final String TIMEOUT_REPORT_ATTENTION_DESCRIPTION = "El reporte no se dió de baja por un error de conexión. Por favor, inténtalo más tarde.";
    public static final String AGU_DESCRIPTION = "Error AGU.";

    public static final String ERROR_GENERIC_PART_1 = "El reporte no se dió de baja por un error";
    public static final String ERROR_GENERIC_PART_2 = ". Por favor, inténtalo más tarde";

    public static final String RESPONSE_ERROR_KEY = "ERROR";

    public static final String KEY_FOR_HASH = "vmYsvKnGnkk9fDi";

    public static final String TAG_REPORT = "_TAG_REPORT";
    public static final String TAG_PROGRAM_DESCRIPTION = "_TAG_PROGRAM_DESCRIPTION";
    public static final String TAG_BACHE = "_TAG_BACHE";
    public static final String TAG_FAQ = "_TAG_FAQ";
    public static final String TAG_NEWS = "_TAG_NEWS";
    public static final String TAG_CONTACT = "_TAG_CONTACT";
    public static final String TAG_PRIVACY = "_TAG_PRIVACY";
    public static final String TAG_TERMS = "_TAG_TERMS";
    public static final String TAG_USER_PROFILE = "_TAG_USER_PROFILE";
    public static final String TAG_HISTORY_DETAIL = "_TAG_HISTORY_DETAIL";
    public static final String TAG_REPORT_DETAIL = "_TAG_REPORT_DETAIL";
    public static final String TAG_PRIMARY_ROUTES = "_TAG_PRIMARY_ROUTES";
    public static final String TAG_PAYMENT = "_TAG_PAYMENT";

    public static final String NEW_REPORT_RESULT_OK_SALESFORCE = "100";
    public static final String NEW_REPORT_RESULT_OK_SERVICE = "200";
    public static final String NEW_REPORT_RESULT_OUT_OF_RANGE = "750";
    public static final String NEW_REPORT_RESULT_ALREADY_REPORTED = "751";

    public static final String FLURRY_API_KEY = "Z87G8JPZH5QBP9V685MJ";
    public static final int AVENIDA_OK = 1;
    public static final int AVENIDA_NOT_VALID = 2;

    public static final String REPORT_CALL = "072";

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_TO_SHOW = "dd/MM/yyyy   HH:mm";
    public static final String DATE_FORMAT_TO_SHOW_STATUS = "dd/MM/yyyy - HH:mm";

    public static final int USER_TYPE_CITIZEN = 4;
    public static final int USER_TYPE_TROOP = 5;

    public static final int REPORT_STATUS_NEW = 1;
    public static final int REPORT_STATUS_ASIGNED = 2;
    public static final int REPORT_STATUS_SOLVED = 3;
    public static final int TROOP_REPORT_STATUS_MARK_AS_TAKEN = 2;
    public static final int TROOP_REPORT_STATUS_DOES_NOT_APPLY = 4;//NO APLICA
    public static final int TROOP_REPORT_STATUS_MARK_AS_REPAIRED = 3;
    public static final int TROOP_REPORT_STATUS_FALSE = 5;
    public static final int TROOP_REPORT_STATUS_WRONG = 6;
    public static final int REPORT_STATUS_7 = 7;
    public static final int REPORT_STATUS_8 = 8;

    public static final int TROOP_SELECT_REPORT_REQUEST_CODE = 6454;

    public static final String PARSE_APP_ID = "44URvb7AMQ55GCPsc6s58AZOFYPWJWW34OIcDwnY";
    public static final String PARSE_CLIENT_KEY = "JDXuA8JtHyAYiyiG3BbZIF5OATp2hTVbaSY3StWe";

    public static final String APPTENTIVE_API_KEY = "be836a1fab71cbd6d4788262fa87d2a41910eaede818f1bf838e669fb12aa86c";

    public static final int VALID_REPORT_REQUEST_CODE = 5990;
    public static final int WRONG_REPORT_REQUEST_CODE = 5991;
    public static final int FAKE_REPORT_REQUEST_CODE = 5992;
    public static final int INVALID_REPORT_REQUEST_CODE = 5997;

    // NUEVOS
    public static final int PENDING_REPORT_REQUEST_CODE = 5993;
    public static final int REALLOCATE_REPORT_REQUEST_CODE = 5994;


    public static final int REPORT_ORIGIN_APP = 1;
    public static final int REPORT_ORIGIN_CMS = 2;
    public static final int REPORT_ORIGIN_072_SALESFORCE = 3;
    public static final int REPORT_ORIGIN_SMS = 5;

    public static final Map<Integer, String> KEY_DELEGATIONS;

    public static final int NO_PICTURE_TO_SHOW = -1;

    static {
        Map<Integer, String> mapValues = new HashMap<>();

        mapValues.put(1, "Álvaro Obregón");
        mapValues.put(2, "Azcapotzalco");
        mapValues.put(3, "Benito Juárez");
        mapValues.put(4, "Coyoacán");
        mapValues.put(5, "Cuajimalpa de Morelos");
        mapValues.put(6, "Cuauhtémoc");
        mapValues.put(7, "Gustavo A. Madero");
        mapValues.put(8, "Iztacalco");
        mapValues.put(9, "Iztapalapa");
        mapValues.put(10, "La Magdalena Contreras");
        mapValues.put(11, "Miguel Hidalgo");
        mapValues.put(12, "Milpa Alta");
        mapValues.put(13, "Tláhuac");
        mapValues.put(14, "Tlalpan");
        mapValues.put(15, "Venustiano Carranza");
        mapValues.put(16, "Xochimilco");
        mapValues.put(17, "No se logro identificar ");
        mapValues.put(18, "No aplica");

        KEY_DELEGATIONS = Collections.unmodifiableMap(mapValues);
    }

    public static final Map<Integer, String> REPORT_STATUS_MAP;

    static {
        Map<Integer, String> statusMapValues = new HashMap<>();

        statusMapValues.put(1, "En espera de solución");
        statusMapValues.put(2, "En revisión");
        statusMapValues.put(3, "Atendido");
        statusMapValues.put(4, "No aplica");
        statusMapValues.put(5, "Bache no encontrado");
        statusMapValues.put(6, "No es un bache");
        statusMapValues.put(7, "Pendiente de solución");
        statusMapValues.put(8, "Reasignado");

        REPORT_STATUS_MAP = Collections.unmodifiableMap(statusMapValues);
    }

//    1  En espera de solución
//    2  En revisión
//    3  Atendido
//    4  No aplica
//    5  Bache no encontrado
//    6  No es un bache
//    7  Pendiente de solución
//    8  Reasignado

    public static final int OLD_VERSION = 100;
    public static final int NEW_VERSION = 200;

    public static final String PRIMARY_YES = "S";
    public static final String PRIMARY_NO = "N";

    public static final Map<String, String> KEY_REPORT_PICTURES;

    static {
        Map<String, String> mapValues = new HashMap<>();

        mapValues.put("bache_picture_1_r.jpeg", "_BACHE_CITIZEN_PIC_1");
        mapValues.put("bache_picture_2_r.jpeg", "_BACHE_CITIZEN_PIC_2");
        mapValues.put("bache_picture_3_r.jpeg", "_BACHE_CITIZEN_PIC_3");
        mapValues.put("bache_picture_4_r.jpeg", "_BACHE_CITIZEN_PIC_4");

        KEY_REPORT_PICTURES = Collections.unmodifiableMap(mapValues);
    }

    public static final int ROAD_TYPE_DEFAULT = 0;
    public static final int ROAD_TYPE_PRIMARY = 1;
    public static final int ROAD_TYPE_SECONDARY = 2;

    public static final int PICTURE_CITIZEN_QUALITY = 10;
    public static final int PICTURE_SQUAD_QUALITY = 50;

    public static final String REPORTE_ATTENDED = "807";

    // PERMISSION REQUEST CODES

    public static final int REQUEST_CAMERA_PERMISSION_CODE = 344;

    public static final int NUMBER_OF_RETRIES = 3;
    public static final int RETRY_INTERVAL_MILLISECONDS = 3000;

    public static final String SQUAD_NOTIFICATION_MESSAGE_ARG = "_SQUAD_NOTIFICATION_MESSAGE_ARG";
    public static final String SQUAD_NOTIFICATION_IS_APP_IN_FOREGROUND = "_SQUAD_NOTIFICATION_IS_APP_IN_FOREGROUND";

    public static final String IS_FROM_NOTIFICATION = "_IS_FROM_NOTIFICATION";
    public static final String NOTIFICATION_BACHE_ID = "_NOTIFICATION_BACHE_ID";

    public static final String BACHE_ID_KEY = "idBache";
    public static final String SQUAD_LONG_MESSAGE_KEY = "mensaje";
}
