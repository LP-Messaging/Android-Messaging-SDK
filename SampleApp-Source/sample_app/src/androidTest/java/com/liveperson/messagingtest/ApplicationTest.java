package com.liveperson.messagingtest;

import android.app.Application;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.test.ApplicationTestCase;

import com.liveperson.infra.messaging_ui.uicomponents.PushMessageParser;
import com.liveperson.infra.model.PushMessage;
import com.liveperson.messaging.sdk.api.LivePerson;

import java.util.Map;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }


    public void testDeprecatedHandlePush(){

        String brandId = "36366796";
        //no unread counter badge in push messages
        Bundle data = createBundleData(brandId, -1);

        LivePerson.handlePush(getContext(), data, brandId, false);

        //fallback will return 1
        assertEquals(LivePerson.getNumUnreadMessages(brandId), 1);

    }

    public void testDeprecatedHandlePushMultiUnread(){
        String brandId = "36366796";
        Bundle data = createBundleData(brandId, 1);

        LivePerson.handlePush(getContext(), data, brandId, false);

        assertEquals(LivePerson.getNumUnreadMessages(brandId), 1);

    }

    public void testDeprecatedHandlePushMultiUnreadFromPush(){
        String brandId = "36366796";
        Bundle data = createBundleData(brandId, 3);

        LivePerson.handlePush(getContext(), data, brandId, false);

        assertEquals(LivePerson.getNumUnreadMessages(brandId), 3);

    }


    /****** NEW HANDLE PUSH API ********/

    public void testHandlePush(){

        String brandId = "36366796";
        Map<String, String> data = createDummyPush(brandId, -1);

        PushMessage message = LivePerson.handlePushMessage(getContext(), data, brandId, false);

        assertNotNull(message);
        assertEquals(message.getCurrentUnreadMessgesCounter(), -1);
        assertEquals(message.getMessage(), "Hi, How are you doing?" );
        assertEquals(message.getFrom(), "Khaleesi");
        assertEquals(message.getBackendService(), "ams");
        assertEquals(message.getBrandId(), brandId);
        assertEquals(message.getCollapseKey(), null);
        assertEquals(message.getConversationId(), "164bc4a3-5147-4f61-b136-0a270e7c64aa");

        //fallback
        assertEquals(LivePerson.getNumUnreadMessages(brandId), 1);

    }

    public void testHandlePushMultiUnread(){
        String brandId = "36366796";
        Map<String, String> data = createDummyPush(brandId, 1);

        PushMessage message = LivePerson.handlePushMessage(getContext(), data, brandId, false);

        assertNotNull(message);
        assertEquals(message.getCurrentUnreadMessgesCounter(), 1);
        assertEquals(message.getMessage(), "Hi, How are you doing?" );
        assertEquals(message.getFrom(), "Khaleesi");
        assertEquals(message.getBackendService(), "ams");
        assertEquals(message.getBrandId(), brandId);
        assertEquals(message.getCollapseKey(), null);
        assertEquals(message.getConversationId(), "164bc4a3-5147-4f61-b136-0a270e7c64aa");
        assertEquals(LivePerson.getNumUnreadMessages(brandId), 1);

    }

    public void testHandlePushMultiUnreadFromPush(){
        String brandId = "36366796";
        Map<String, String> data = createDummyPush(brandId, 3);

        PushMessage message = LivePerson.handlePushMessage(getContext(), data, brandId, false);

        assertNotNull(message);
        assertEquals(message.getCurrentUnreadMessgesCounter(), 3);
        assertEquals(message.getMessage(), "Hi, How are you doing?" );
        assertEquals(message.getFrom(), "Khaleesi");
        assertEquals(message.getBackendService(), "ams");
        assertEquals(message.getBrandId(), brandId);
        assertEquals(message.getCollapseKey(), null);
        assertEquals(message.getConversationId(), "164bc4a3-5147-4f61-b136-0a270e7c64aa");
        assertEquals(LivePerson.getNumUnreadMessages(brandId), 3);

    }


    public void testHandlePushWrongName(){
        String brandId = "36366796";
        Map<String, String> data = createDummyBadFromPush(brandId, -1);

        PushMessage message = LivePerson.handlePushMessage(getContext(), data, brandId, false);

        assertNotNull(message);
        assertEquals(message.getCurrentUnreadMessgesCounter(), -1);
        assertEquals(message.getMessage(), "Khaleesi Hi, How are you doing?" );
        assertEquals(message.getFrom(), "");
        assertEquals(message.getBackendService(), "ams");
        assertEquals(message.getBrandId(), brandId);
        assertEquals(message.getCollapseKey(), null);
        assertEquals(message.getConversationId(), "164bc4a3-5147-4f61-b136-0a270e7c64aa");
        assertEquals(LivePerson.getNumUnreadMessages(brandId), 1);

    }



    public void testHandlePushNull(){
        String brandId = "36366796";
        Map<String, String> data = null;

        PushMessage message = LivePerson.handlePushMessage(getContext(), data, brandId, false);
        assertNull(message);
    }

    private Bundle createBundleData(String brandId, int unreadMessages) {

        java.util.Map<String, String> data = createDummyPush(brandId, unreadMessages);
        return PushMessageParser.convertDataToBundle(data);
    }

    @NonNull
    private Map<String, String> createDummyPush(String brandId, int unreadMessages) {
        Map<String,String> data = new android.support.v4.util.ArrayMap<>();
        if (unreadMessages > -1){
            data.put("payload", "{\"conversationId\":\"164bc4a3-5147-4f61-b136-0a270e7c64aa\",\"brandId\":\""+brandId+"\",\"backendService\":\"ams\",\"originatorId\":\"36366796.627949310\",\"badge\":\""+unreadMessages+"\"}");
        }else{
            data.put("payload", "{\"conversationId\":\"164bc4a3-5147-4f61-b136-0a270e7c64aa\",\"brandId\":\""+brandId+"\",\"backendService\":\"ams\",\"originatorId\":\"36366796.627949310\"}");
        }
        data.put("message", "Khaleesi: Hi, How are you doing?");
        return data;
    }

    @NonNull
    private Map<String, String> createDummyBadFromPush(String brandId, int unreadMessages) {
        Map<String,String> data = new android.support.v4.util.ArrayMap<>();
        if (unreadMessages > -1){
            data.put("payload", "{\"conversationId\":\"164bc4a3-5147-4f61-b136-0a270e7c64aa\",\"brandId\":\""+brandId+"\",\"backendService\":\"ams\",\"originatorId\":\"36366796.627949310\",\"badge\":\""+unreadMessages+"\"}");
        }else{
            data.put("payload", "{\"conversationId\":\"164bc4a3-5147-4f61-b136-0a270e7c64aa\",\"brandId\":\""+brandId+"\",\"backendService\":\"ams\",\"originatorId\":\"36366796.627949310\"}");
        }
        data.put("message", "Khaleesi Hi, How are you doing?");
        return data;
    }
}