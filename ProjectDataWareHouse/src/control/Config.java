package control;

public class Config {
	// Các trường của Loan
	private int idConf;
	private String configName;// Trường này là tên của config ứng với kiểu file
								// vd:f_txt.
	private String serverSou;
	private int port;
	private String userSou;
	private String passSou;
	private String dirSou;
	private String fieldName;
	private String delimeterSou;
	private String formatSou;
	private String serverDes;
	private String DBNameDes;
	private String useDes;
	private String passDes;
	// Trường này trở đi là có trong config của Phượng
	private String targetTable;
	private String fileType;
	private String importDir;
	private String successDir;
	private String errorDir;

	public Config() {

	}

	public int getIdConf() {
		return idConf;
	}

	public String getConfigName() {
		return configName;
	}

	public String getServerSou() {
		return serverSou;
	}

	public int getPort() {
		return port;
	}

	public String getUserSou() {
		return userSou;
	}

	public String getPassSou() {
		return passSou;
	}

	public String getDirSou() {
		return dirSou;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getDelimeterSou() {
		return delimeterSou;
	}

	public String getFormatSou() {
		return formatSou;
	}

	public String getServerDes() {
		return serverDes;
	}

	public String getDBNameDes() {
		return DBNameDes;
	}

	public String getUseDes() {
		return useDes;
	}

	public String getPassDes() {
		return passDes;
	}

	public String getTargetTable() {
		return targetTable;
	}

	public String getFileType() {
		return fileType;
	}

	public String getImportDir() {
		return importDir;
	}

	public String getSuccessDir() {
		return successDir;
	}

	public String getErrorDir() {
		return errorDir;
	}


	public void setIdConf(int idConf) {
		this.idConf = idConf;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public void setServerSou(String serverSou) {
		this.serverSou = serverSou;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setUserSou(String userSou) {
		this.userSou = userSou;
	}

	public void setPassSou(String passSou) {
		this.passSou = passSou;
	}

	public void setDirSou(String dirSou) {
		this.dirSou = dirSou;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setDelimeterSou(String delimeterSou) {
		this.delimeterSou = delimeterSou;
	}

	public void setFormatSou(String formatSou) {
		this.formatSou = formatSou;
	}

	public void setServerDes(String serverDes) {
		this.serverDes = serverDes;
	}

	public void setDBNameDes(String dBNameDes) {
		DBNameDes = dBNameDes;
	}

	public void setUseDes(String useDes) {
		this.useDes = useDes;
	}

	public void setPassDes(String passDes) {
		this.passDes = passDes;
	}

	public void setTargetTable(String targetTable) {
		this.targetTable = targetTable;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public void setImportDir(String importDir) {
		this.importDir = importDir;
	}

	public void setSuccessDir(String successDir) {
		this.successDir = successDir;
	}

	public void setErrorDir(String errorDir) {
		this.errorDir = errorDir;
	}



}
