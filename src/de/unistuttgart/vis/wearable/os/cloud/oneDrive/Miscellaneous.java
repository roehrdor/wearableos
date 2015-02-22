package de.unistuttgart.vis.wearable.os.cloud.oneDrive;

/**
 * Class to provide information crucial for One Drive to work, including the
 * Client-Id of the "App" created via the developer site of One Drive and the
 * keys to opt the associated values from the received JSON results
 * 
 */
public class Miscellaneous {

	public static final String[] SCOPES = { "wl.skydrive" };
	// TODO remove when releasing source code
	public static final String CLIENT_ID = "000000004C129114";
    private static final String CLOUD_DB_FOLDER_NAME = "Garment-OS";
	private Miscellaneous() {
		throw new AssertionError();
	}
    private static final String CLOUD_DB_NAME = "gos_sensors";
	public static final String CODE = "code";
	public static final String DATA = "data";
	public static final String DESCRIPTION = "description";
	public static final String ERROR = "error";
	public static final String EMAIL_HASHES = "email_hashes";
	public static final String FIRST_NAME = "first_name";
	public static final String GENDER = "gender";
	public static final String ID = "id";
	public static final String IS_FAVORITE = "is_favorite";
	public static final String IS_FRIEND = "is_friend";
	public static final String LAST_NAME = "last_name";
	public static final String LOCALE = "locale";
	public static final String LINK = "link";
	public static final String MESSAGE = "message";
	public static final String NAME = "name";
	public static final String UPDATED_TIME = "updated_time";
	public static final String USER_ID = "user_id";
	public static final String PERMISSIONS = "permissions";
	public static final String IS_DEFAULT = "is_default";
	public static final String FROM = "from";
	public static final String SUBSCRIPTION_LOCATION = "subscription_location";
	public static final String CREATED_TIME = "created_time";
	public static final String LOCATION = "location";
	public static final String TYPE = "type";
	public static final String PARENT_ID = "parent_id";
	public static final String SOURCE = "source";
	public static final String UPLOAD_LOCATION = "upload_location";
	public static final String FOLDER = "folder";

    public static String getCloudDbFolderName(){
        return CLOUD_DB_FOLDER_NAME;
    }
    public static String getCloudDbName(){
        return CLOUD_DB_NAME;
    }

}
