package se.lisaannica.stopmotion;

import java.io.File;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.conf.Configuration;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.sax.StartElementListener;
import android.util.Log;
import se.lisaannica.stopmotion.R;

/**
 * This test file posts a picture on TwitPic and a tweet on twitter linking to that picture
 * It uses twitter4j( http://twitter4j.org/ ), which is excellent. 
 * Using this requires registering at http://www.twitter.com and http://www.twitpic.com for
 * authentication data.
 * The class needs TWO jar-files in build path that also gets exported to apk file.
 * twitter4j-core-android-VERSION.jar
 * twitter4j-media-support-android-VERSION.jar
 * In this example VERSION = 2.2.6
 * 
 * The manifest file needs TWO permissions for this example.
 * uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
 * uses-permission android:name="android.permission.INTERNET"
 * 
 * The example uses a picture file in the external images folder called /test/test.png  
 * 
 * WRITE_EXTERNAL_STORAGE Is needed to fetch the png from external storage.
 * INTERNET Is needed for twitter publishing and uploading to twitpic.
 * @author TorSterner http://www.torsterner.se/blog
 *
 */
public class TwitterConnection {

	private String twitpic_api_key;
	private String oauth_consumer_key;
	private String oauth_consumer_secret;
	private Twitter mTwitter;
	private RequestToken mRequestToken;
	private AccessToken mAccessToken;

	public TwitterConnection(Resources res) {
		twitpic_api_key = res.getString(R.string.twitpic_api_key);
		oauth_consumer_key = res.getString(R.string.twitter_consumer_key);
		oauth_consumer_secret = res.getString(R.string.twitter_consumer_secret);
	}

	/**
	 * This methods uploads a picture to twitpic and returns the twitpic url.
	 * @param configuration 
	 * @param file - Image file.
	 * @return url if successful, null if it fails.
	 * @throws TwitterException If auth fails or some other uploading error.
	 */
	public String uploadTwitPic(Configuration configuration, File file)
			throws TwitterException {
		String url = null;
		if (file != null) {
			// Use ImageUploadFactory with the configuration object to upload picture to TwitPic.
			ImageUpload upload = new ImageUploadFactory(configuration).getInstance(MediaProvider.TWITPIC);
			url = upload.upload(file);
		}
		return url;
	}

	public String setup() throws TwitterException {
		// TODO Auto-generated method stub
		ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	        .setOAuthConsumerKey(oauth_consumer_key) // Twitter Consumer
	        .setOAuthConsumerSecret(oauth_consumer_secret); // Twitter Consumer
	    Configuration configuration = cb.build(); // Build the Configuration that must be 
	    // used when creating the Twitter Object 
	    // as well as uploading the image.

	    // Create the Twitter object used to send stuff to.
	    mTwitter = new TwitterFactory(configuration).getInstance();
	    mRequestToken = mTwitter.getOAuthRequestToken();

	    return mRequestToken.getAuthorizationURL();
	}

	public void authenticate(String pin) throws TwitterException {
		mAccessToken = mTwitter.getOAuthAccessToken(mRequestToken, pin);
	}

	public String uploadImage(File file) throws TwitterException {
		// TODO Auto-generated method stub
		// A builder for the configuration with all authentications needed, both twitter and twitpic have
		// keys that must be provided here 
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setMediaProviderAPIKey(twitpic_api_key) // This is the TwitPic Key
		.setOAuthConsumerKey(oauth_consumer_key) // Twitter Consumer
		.setOAuthConsumerSecret(oauth_consumer_secret) // Twitter Consumer
		.setOAuthAccessToken(mAccessToken.getToken()) // Twitter User Token
		.setOAuthAccessTokenSecret(mAccessToken.getTokenSecret()); // Twitter User Token
		Configuration configuration = cb.build(); // Build the Configuration that must be 
		// used when creating the Twitter Object 
		// as well as uploading the image.

		// Create the Twitter object used to send stuff to.
//		TwitterFactory tf = new TwitterFactory(configuration);
//		Twitter userTwitter = tf.getInstance();
		// Use our own method for uploading pic to TwitPic
		return uploadTwitPic(configuration, file);
	}

	public Status sendTweet(String tweetMsg) throws TwitterException {
		// TODO Auto-generated method stub
		// Create the message string, should include the url to the TwitPic 
	      // image so it will be pasted as a link

	      // Post the message on twitter. Returns a Status object.
	      Status status = mTwitter.updateStatus(tweetMsg);
	      return status;
	}
}