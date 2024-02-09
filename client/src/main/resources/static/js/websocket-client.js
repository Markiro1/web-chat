const TypeOfMessage = {
    ON_CONNECTION: "ON_CONNECTION",
    SEND_TO_ALL: "SEND_TO_ALL",
    ON_CLOSE: "ON_CLOSE",
    REQUEST_USER_LIST: "REQUEST_USER_LIST",
    SEND_TO_ONE_PERSON: "SEND_TO_ONE_PERSON"
};

let userMap = new Map();
let chatMap = new Map();
let currentChatId = 1;
let currentRecipientId = null;

const tokenData = parseJwt(token);
let currentUser = tokenData.username;
let currentUserId = tokenData.id;


function parseJwt(token) {
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));

        return JSON.parse(jsonPayload);
    } catch (error) {
        console.error("Error parsing JWT", error);
        return null;
    }

}


document.addEventListener("DOMContentLoaded", function () {
    const wsUri = "ws://localhost:8080/ws";
    const storedSocketUrl = sessionStorage.getItem("socketUrl");
    const webSocket = storedSocketUrl ? new WebSocket(storedSocketUrl) : new WebSocket(wsUri);


    const sendButton = document.getElementById('send-button');
    sendButton.addEventListener('click', async function () {
        await sendMessage(messageInput, TypeOfMessage.SEND_TO_ALL);
    });


    const messageInput = document.getElementById('message-input');
    messageInput.addEventListener('keydown', async function (event) {
        if (event.key === "Enter") {
            await sendMessage(messageInput, TypeOfMessage.SEND_TO_ALL);
        }
    })


    window.addEventListener("beforeunload", function () {
        sendMessageToServer(TypeOfMessage.ON_CLOSE);
    });

    window.addEventListener("load", function () {
        if (webSocket.readyState === WebSocket.CLOSED) {
            window.location.replace("/");
        }
    });


    webSocket.onclose = function (event) {
        sendMessageToServer(TypeOfMessage.ON_CLOSE);
    };

    webSocket.onopen = async function (event) {
        if (token) {
            sessionStorage.setItem("socketUrl", webSocket.url);
            sendMessageToServer(TypeOfMessage.ON_CONNECTION);
            sendMessageToServer(TypeOfMessage.REQUEST_USER_LIST);
            await getChatHistories();
        } else {
            window.location.replace("/");
        }
    };

    async function getChatHistories() {
        const data = await sendHttpGetRequest("http://localhost:8080/chat/history/" + currentUserId);
        console.log(data)
        if (data) {
            saveMessageToMap(data);
            updateChatMessages(chatMap);
        }

        console.log("GET CHAT HISTORIES")
    }

    async function getChatId(recipientId) {
        const data = await sendHttpGetRequest(
            "http://localhost:8080/chat/privateChat/"
            + currentUserId
            + "/"
            + recipientId);
        if (data) {
            currentChatId = parseInt(data);
            currentRecipientId = recipientId;
        }
    }

    async function sendHttpPostRequest(url, data) {
        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(data),
            });
            if (!response.ok) {
                const errorText = await response.json();
                console.log(errorText)
                return { error: errorText };
            }
            const responseData = await response.text();
            return responseData;
        } catch (error) {
            return { error: 'Network error' };
        }
    }

    async function sendHttpGetRequest(url) {
        try {
            const response = await fetch(url, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
            });
            if (!response.ok) {
                const errorText = await response.json();
                console.log(errorText)
                return { error: errorText };
            }
            const data = await response.text();
            return data;
        } catch (error) {
            console.error('ERROR POST REQUEST:', error)
        }
    }


    webSocket.onmessage = function (event) {
        const message = event.data;
        const userCountElement = document.getElementById('userCount');
        const userListElement = document.getElementById('userList')

        const messageHeader = message.split(":")[0];
        switch (messageHeader) {
            case "USER_COUNT":
                const count = parseInt(message.split(":")[1]);
                userCountElement.textContent = String(count);
                break;
            case "USER_LIST":
                const response = message.substring("USER_LIST:".length);
                saveUsersToMap(response);
                updateUserList(userListElement, userMap);
                break;
            case "NEW_MESSAGE":
                const newMessage = message.substring("NEW_MESSAGE:".length);
                handleNewMessage(newMessage)
                updateChatMessages(chatMap);
                break;
        }
    };

    function handleNewMessage(newMessage) {
        const messageObj = JSON.parse(newMessage);
        const chatId = messageObj.chatId;
        const userId = messageObj.userId;
        const messageText = messageObj.message;
        const username = userMap.get(userId.toString()) || 'Unknown User';

        const formattedMessage = `${username}:${messageText}`;
        if (!chatMap.has(chatId)) {
            chatMap.set(chatId, []);
        }
        chatMap.get(chatId).push(formattedMessage);

        if (currentChatId === chatId) {
            updateChatMessages(chatMap);
        }
    }

    function saveUsersToMap(response) {
        const usersObject = JSON.parse(response);
        for (const userId in usersObject) {
            if (usersObject.hasOwnProperty(userId)) {
                userMap.set(userId, usersObject[userId]);
            }
        }
    }

    function saveMessageToMap(jsonMessageList) {
        let messageList = JSON.parse(jsonMessageList);
        messageList.forEach(message => {
            const formattedText = `${message.username}:${message.text}`;
            if (!chatMap.has(message.chatId)) {
                chatMap.set(message.chatId, []);
            }
            chatMap.get(message.chatId).push(formattedText);
        })
    }


    function updateUserList(userListElement, userMap) {
        while (userListElement.firstChild) {
            userListElement.removeChild(userListElement.firstChild);
        }
        const userListUl = document.createElement('ul')

        userMap.forEach((value, key) => {
            if (key != currentUserId) {
                const userLi = document.createElement('li');
                userLi.textContent = value;
                userLi.classList.add('user-item');
                userListElement.appendChild(userLi);
                userLi.addEventListener('click', async () => {
                    await getChatId(key);
                    openPrivateChat(value);
                });
            }
        })
        userListElement.appendChild(userListUl);
    }


    function openPrivateChat(username) {
        document.getElementById('chatWith').textContent = "Chat with: " + username;

        const modal = document.getElementById('privateChatModal');
        modal.style.display = "block";

        const chatMessagesContainer = document.getElementById('privateChatMessages');
        chatMessagesContainer.innerHTML = '';

        displayPrivateChatMessages(chatMap, currentChatId, chatMessagesContainer);

        const privateMessageInput = document.getElementById('privateMessageInput');
        privateMessageInput.addEventListener('keydown', function (event) {
            if (event.key === "Enter") {
                sendMessage(privateMessageInput, TypeOfMessage.SEND_TO_ONE_PERSON);

            }
        });

        const sendButton = document.getElementById('sendPrivateMessageButton');
        sendButton.onclick = function () {
            sendMessage(privateMessageInput, TypeOfMessage.SEND_TO_ONE_PERSON);
        };

        const closeButton = modal.querySelector('.close');
        closeButton.onclick = function () {
            modal.style.display = "none";
            currentChatId = 1;
            currentRecipientId = null;
        };
    }

    function updateChatMessages(chatMap) {
        if (currentChatId === 1) {
            const chatMessages = document.getElementById('chat-messages');
            chatMessages.innerHTML = '';
            const messages = chatMap.get(currentChatId);
            messages.forEach(msg => {
                const messageDiv = document.createElement('div');
                messageDiv.textContent = msg;
                chatMessages.appendChild(messageDiv);
            });
            chatMessages.scrollTop = chatMessages.scrollHeight;
        } else {
            const chatMessagesContainer = document.getElementById('privateChatMessages');
            chatMessagesContainer.innerHTML = '';
            const messages = chatMap.get(currentChatId);
            messages.forEach(msg => {
                const messageDiv = document.createElement('div');
                messageDiv.textContent = msg;
                chatMessagesContainer.appendChild(messageDiv);
            });
            chatMessagesContainer.scrollTop = chatMessagesContainer.scrollHeight;
        }
    }


    function displayPrivateChatMessages(chatMap, chatId, container) {
        if (chatMap.has(chatId)) {
            const messages = chatMap.get(chatId);
            messages.forEach(msg => {
                const messageDiv = document.createElement('div');
                messageDiv.textContent = msg;
                container.appendChild(messageDiv);
            });
        }
    }

   function sendMessageToServer(typeOfMessage) {
        const message = {
            userId: currentUserId,
            chatId: currentChatId,
            type: typeOfMessage
        };
        webSocket.send(JSON.stringify(message));
        console.log("SEND MESSAGE TO SERVER")
    }

    function sendMessage(messageInput, type) {
        const messageText = messageInput.value.trim();
        if (messageText !== '') {
            const message = {
                userId: currentUserId,
                chatId: currentChatId,
                type: type,
                message: messageText,
                recipientId: currentRecipientId
            };
            sendHttpPostRequest('http://localhost:8080/message/save', message)
                .then(response => {
                    if (response.error) {
                        const errorMessageElement = document.getElementById('errorMessage');
                        errorMessageElement.textContent = response.error;
                    } else {
                        messageInput.value = '';
                    }
                })
                .catch(error => {
                    console.error('Failed to send message:', error);
                });
        }
    }
});