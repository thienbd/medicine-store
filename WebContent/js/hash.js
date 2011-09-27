/**
 * 
 */
function hashdata(username, password, element) {
	var hashvalue = calcMD5(username.getValue() + password.getValue());
	var shaObj = new jsSHA(hashvalue, "ASCII");
	element.setValue(shaObj.getHash("SHA-384", "HEX"));
	element.fireOnChange();
}

function hashpassword(password, element) {
	var hashvalue = calcMD5(password.getValue());
	var shaObj = new jsSHA(hashvalue, "ASCII");
	element.setValue(shaObj.getHash("SHA-384", "HEX"));
	element.fireOnChange();
}