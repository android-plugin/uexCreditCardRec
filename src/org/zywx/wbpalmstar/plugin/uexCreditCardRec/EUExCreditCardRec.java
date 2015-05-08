package org.zywx.wbpalmstar.plugin.uexCreditCardRec;

import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

import org.zywx.wbpalmstar.engine.EBrowserView;
import org.zywx.wbpalmstar.engine.universalex.EUExBase;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

public class EUExCreditCardRec extends EUExBase {

	private int MY_SCAN_REQUEST_CODE = 100; // arbitrary int
	public static final String func_on_cbLockAppState = "javascript:uexCreditCardRec.callBackCreditCard";
	public static final String cbCreditCardFunName = "javascript:uexCreditCardRec.cbCreditCard";
	public EUExCreditCardRec(Context context, EBrowserView inparent) {
		super(context, inparent);
	}

	@Override
	protected boolean clean() {
		return false;
	}

	public void openCreditCardRec(String[] params) {
		if (params.length < 1) {
			return;
		}
		if (!TextUtils.isEmpty(params[0])) {
			onScanPress(params[0]);
		}
	}

	private void onScanPress(String token) {
		// This method is set up as an onClick handler in the layout xml
		// e.g. android:onClick="onScanPress"
		Intent scanIntent = new Intent(mContext, CardIOActivity.class);
		// required for authentication with card.io
		scanIntent.putExtra(CardIOActivity.EXTRA_APP_TOKEN, token);
		// customize these values to suit your needs.
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default:
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default:
		scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_ZIP, false); // default:
		scanIntent.putExtra(CardIOActivity.EXTRA_USE_CARDIO_LOGO, true); // default:
		// false
		// hides the manual entry button
		// if set, developers should provide their own manual entry mechanism in
		// the app
		scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default:
		// MY_SCAN_REQUEST_CODE is arbitrary and is only used within this
		// activity.
		startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String resultStr;
		if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {
			CreditCard scanResult = data
					.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
			// Never log a raw card number. Avoid displaying it, but if
			// necessary use getFormattedCardNumber()
			resultStr = "Card Number: " + scanResult.getFormattedCardNumber()
					+ "\n";
			// Do something with the raw number, e.g.:
			// myService.setCardNumber( scanResult.cardNumber );

			if (scanResult.isExpiryValid()) {
				resultStr += "Expiration Date: " + scanResult.expiryMonth + "/"
						+ scanResult.expiryYear + "\n";
			}
			if (scanResult.cvv != null) {
				// Never log or display a CVV
				resultStr += "CVV has " + scanResult.cvv.length()
						+ " digits.\n";
			}

			if (scanResult.zip != null) {
				resultStr += "Zip: " + scanResult.zip + "\n";
			}
			String jsCallBack = func_on_cbLockAppState + "('"
					+ scanResult.getFormattedCardNumber() + "','"
					+ scanResult.expiryMonth + "/" + scanResult.expiryYear
					+ "','" + scanResult.cvv + "')";
			onCallback(jsCallBack);

			jsCallBack = cbCreditCardFunName + "('" + scanResult.getRedactedCardNumber() + "','" + scanResult.expiryMonth + "/" + scanResult.expiryYear + "','" + scanResult.cvv + "')";
            		onCallback(jsCallBack);
		} else {
			resultStr = "Scan was canceled.";
			String jsCallBack = func_on_cbLockAppState + "('0','0','0')";
			onCallback(jsCallBack);

			jsCallBack = cbCreditCardFunName + "('0','0','0')";
            		onCallback(jsCallBack);
		}
		Log.i("uexCreditCardRec", resultStr);
	}
}