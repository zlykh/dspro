class ProfileInfo extends React.Component {
    constructor(props) {
        super(props);
        this.ctn = -1;

    }

    render() {
        return (
            <div className="col-md-4">
                <img src="img/user_placeholderXXXX.png"
                     className="center-block rounded-circle img-fluid my-3"/>
                <div>
                    <p>ctn: {this.props.prof.ctn}</p>

                </div>
            </div>
        );
    }
}

class ApiWrapper extends React.Component {
    constructor(props) {
        super(props);
      //  https://spring.io/guides/gs/messaging-stomp-websocket/
        var socket = new SockJS('localhost:8082/gschat');
        this.stompClient = Stomp.over(socket);
        var that = this;
        this.stompClient.connect({}, function (frame) {
           // setConnected(true);
            that.stompClient.subscribe('/for/ctn123', function (message) {
              //  console.log('message: ' + message);
            });
            console.log('XXXXXXXXXXXXXXXxx: ' + frame);

        });

        // var socket = new WebSocket("ws://localhost:8082/chat");
        // socket.onopen = function() {
        //     alert("Соединение установлено.");
        // };
        //
        // socket.onclose = function(event) {
        //     if (event.wasClean) {
        //         alert('Соединение закрыто чисто');
        //     } else {
        //         alert('Обрыв соединения'); // например, "убит" процесс сервера
        //     }
        //     alert('Код: ' + event.code + ' причина: ' + event.reason);
        // };
        //
        // socket.onmessage = function(event) {
        //     alert("Получены данные " + event.data);
        // };
        //
        // socket.onerror = function(error) {
        //     alert("Ошибка " + error.message);
        // };
        // this.state = {
        //     prof: {
        //         ctn: "999"
        //     }
        // };

    }

    sendMsg() {
        this.stompClient.send("/app/for/ctn123", {}, JSON.stringify({'msg': "vasyan"}));
        //socket.on('send:message', this._messageRecieve);

    }
    //
    // _messageRecieve(message) {
    //     var {messages} = this.state;
    //     messages.push(message);
    //     this.setState({messages});
    // }
    //
    // handleMessageSubmit(message) {
    //     var {messages} = this.state;
    //     messages.push(message);
    //     this.setState({messages});
    //     socket.emit('send:message', message);
    // }


    render() {
        return (
            <div>
                <div className="row">
                    <h2>Profile Setting</h2>
                    asd
                    <button className="btn btn-info" onClick={() => this.sendMsg()}>reload</button>

                </div>
            </div>
        )
    }
}


class XXXX extends React.Component {
    constructor(props) {
        super(props);

    }

    render() {
        return (
            <tr>
                <td>{this.props.k}</td>
                <td>{this.props.v}</td>
            </tr>
        );
    }
}

ReactDOM.render(
    <ApiWrapper />, document.getElementById('root')
);