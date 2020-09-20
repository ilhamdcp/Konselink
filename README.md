# Code Documentation
The documentation of Android provided consists of architectural pattern implemented, library used, how websocket works, and the folder structure

## Android Architecture
THe Android architecture implements Model-View-ViewModel (MVVM) architectural pattern. The diagram could be seen below.

![Imgur](https://i.imgur.com/BHy2IeG.png)

The architecture also implement the repository as and intermediary between data source and view model. Currently, the repository contains live data variables model as seen below.

![Imgur](https://imgur.com/2M3oi4w.png)

Those variables are referenced by live data variables inside view model

## Library Used
This application depends on some key libraries to support chat and websocket. Other libraries such as Room, Retroit, or Mockito, even though being used, will not be explained.

### 1. ChatKit
ChatKit is a user interface library for chat page. This library was made by stfalcon-studio. The GitHub page can be accessed [here](https://github.com/stfalcon-studio/ChatKit). Right now, ChatKit library was converted as a supporting module inside this application project because there was a bug found where this library is using a deprecated Android XML layout.

### 2. CalendarView
CalendarView is a user interface library for displaying calendar layout created by kizitonwose. The calendar layout is used in psikolog application to view consultation schedule. The GitHub page can be accessed [here](https://github.com/kizitonwose/CalendarView).

### 3. Socket.io
Socket.io is a library used to support websocket protocol. This application is using a websocket protocol to chat and receive push notifications. The webpage of native Socket.io can be accessed [here](https://socket.io/blog/native-socket-io-and-android/). The code implementation using Socket.io could be seen in `ChatFragment`.

## How Websocket works
Websocket is used in two different feature. Those features are chat and push notification. The websocket used in chat is integrated with ChatKit. The simple abstraction is that, add any message received from websocket to ChatKit's adapter. Also, put any message sent to websocket to ChatKit's adapter. There is no cache mechanism for chat history yet. Currently, chat history retrieved every time user open the chat page. This procedure resulting blank page for some time every time user opens it.

Websocket also used to receive push notifications. This procedure is an alternative to using Firebase. There is a current obstacle that occurs because of Android's system. The service is stopped by Android to preserve Android's resources. There maybe a temporary solution that could be approached by using Android's WorkManager or AlarmManager to restart the service and retrieve any missing chat during that interval.

## Folder Structure
The folder is pretty concise where each folder represents different Android component. Activity, ViewModel, Repository, etc. For Activity, if that activity contains fragment, then the Activity should be placed inside the folder named by that activity and also containing the fragments.

The file `Utils.kt` contains any static method that could be referenced by kotlin code or act as a `BindingAdapter` for XML layout. While, the `ApplicationDatabase.kt` act as a database instance for the application.


