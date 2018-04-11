
class ProfileInfo extends React.Component {
    constructor(props) {
        super(props);
        this.ctn = 0;
    }

    render() {
        return (
            <div className="col-md-4">
                <img src="img/user_placeholderXXXX.png"
                     className="center-block rounded-circle img-fluid my-3"/>
                <div>
                    <h3>{this.props.prof.name}</h3>
                    <p>{this.props.prof.ctn}</p>
                    <p>{this.props.prof.age}</p>
                    <input type="text" onChange={(event) => this.ctn = event.target.value} className="form-control"
                           placeholder="ctn"/>
                    <button className="btn btn-info" onClick={() => this.props.loadprofcallback(this.ctn)}>reload</button>
                </div>
            </div>
        );
    }
}

class ProfileEditor extends React.Component {
    constructor(props) {
        super(props);
        this.saveProfile = this.saveProfile.bind(this);
        this.handleChange = this.handleChange.bind(this);
            this.request = {};

    }



    saveProfile(ctn) {
        var request = this.request;
        console.log("prep send "+ JSON.stringify(request));
        $.ajax({
            type:"POST",
            url: "http://localhost:8080/profile/" + ctn,
            data: JSON.stringify(request),
            contentType: "application/json; charset=utf-8"
        }).then(function (data) {
            console.log("ok");
        });
    }

    handleChange(event) {
        console.log("event " + event);

        var evtName =event.target.name;
        if(evtName == "hobby" || evtName == "aims" || evtName == "job"){
            this.request[evtName] = event.target.value.split(",");
            this.request[evtName] = this.request[evtName].map(function(s){
                return s.trim();
            });
        }else{
            this.request[evtName] = event.target.value;
        }
        console.log(this.request);
        event.preventDefault();

    }

    render() {
        this.request = this.props.prof;
        var hobby = this.props.prof.hobby ? this.props.prof.hobby.join(",") : "";
        var aims = this.props.prof.aims ? this.props.prof.aims.join(",") : "";
        var job = this.props.prof.job ? this.props.prof.job.join(",") : "";
        return (
            <div className="col-md-4">
                <input type="text" defaultValue={this.props.prof.name} onChange={this.handleChange} name="name" placeholder="name"/>
                <input type="text" defaultValue={this.props.prof.age} onChange={this.handleChange} name="age" placeholder="age"/>
                <input type="text" defaultValue={this.props.prof.city} onChange={this.handleChange} name="city" placeholder="city"/>
                <input type="text" defaultValue={hobby} onChange={this.handleChange} name="hobby" placeholder="hobby"/>
                <input type="text" defaultValue={aims} onChange={this.handleChange} name="aims" placeholder="aims"/>
                <input type="text" defaultValue={job} onChange={this.handleChange} name="job" placeholder="job"/>
                <br/>
                <button className="btn btn-info" onClick={() => this.saveProfile(this.props.prof.ctn)}>save</button>

            </div>
        );
    }
}

class DummyRow extends React.Component {
    render() {
        var dummy = ["aaaaaaaaa", "bbbbbbbbbb", "cccccccccccc"];
        var listItems = dummy.map((number) =>
            <li>{number}</li>
        );
        return (
            <div className="col-md-4">
                <ul>{listItems}</ul>
            </div>
        );
    }
}

class Wrapper extends React.Component {
    constructor(props) {
        super(props);
        this.loadProfile = this.loadProfile.bind(this);
        this.state = {
            prof: {
                ctn: "42",
                name: "test",
                age: 99,
                city: "vrn",
                hobby: ["yes","no"]
            }
        };

    }
    loadProfile(ctn) {
        var self = this;
        $.ajax({
            url: "http://localhost:8080/profile/" + ctn
        }).then(function (data) {
            console.log(data);
            self.setState({prof: data});
        });
    }

    render() {
        return (
            <div>
                <ProfileInfo prof={this.state.prof} loadprofcallback={this.loadProfile}/>
                <ProfileEditor prof={this.state.prof}/>
                <DummyRow />
            </div>
        );
    }
}

var profile = {
    ctn: "123",
    name: "vas",
    age: 23
};
ReactDOM.render(
    // <EmployeeTable employees={EMPLOYEES} />, document.getElementById('root')
    <Wrapper />, document.getElementById('root')
);