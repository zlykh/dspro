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
                    <h3>{this.props.prof.name}</h3>
                    <p>ctn: {this.props.prof.ctn}</p>
                    <p>age: {this.props.prof.age}</p>
                    <input type="text" onChange={(event) => this.ctn = event.target.value} className="form-control"
                           placeholder="ctn"/>
                    <button className="btn btn-info" onClick={() => this.props.loadprofcallback(this.ctn)}>reload
                    </button>
                </div>
            </div>
        );
    }
}

class ProfileEditor extends React.Component {
    constructor(props) {
        super(props);
        this.handleChange = this.handleChange.bind(this);
    }


    handleChange(event) {
        //  event.preventDefault();

        var newprof = this.props.prof;
        var evtName = event.target.name;
        if (evtName == "hobby" || evtName == "aims" || evtName == "job") {
            newprof.tags[evtName] = event.target.value.split(",");
            newprof.tags[evtName] = newprof.tags[evtName].map(function (s) {
                return s.trim();
            });
        } else {
            newprof[evtName] = event.target.value;
        }

        console.log("newprof " + JSON.stringify(newprof));

        this.props.updateprofcallback(newprof);

    }

    render() {
        console.log("render  " + JSON.stringify(this.props.prof));

        var hobby = this.props.prof.tags["hobby"] ? this.props.prof.tags["hobby"].join(",") : "";
        var aims = this.props.prof.tags["aims"] ? this.props.prof.tags["aims"].join(",") : "";
        var job = this.props.prof.tags["job"] ? this.props.prof.tags["job"].join(",") : "";


        console.log("console log ." + hobby);

        return (
            <div className="col-md-4">
                <div className="row">
                    <label className="col-md-4">Name</label>
                    <input className="col-md-8" type="text" name="name" placeholder="name"
                           value={this.props.prof.name} onChange={this.handleChange}/>
                </div>
                <div className="row">
                    <label className="col-md-4">Age</label>
                    <input className="col-md-8" type="text" placeholder="age" name="age"
                           value={this.props.prof.age} onChange={this.handleChange}/>
                </div>
                <div className="row">
                    <label className="col-md-4" for="city">City</label>
                    <input className="col-md-8" type="text" name="city" placeholder="city" id="city"
                           value={this.props.prof.city} onChange={this.handleChange}/>
                </div>
                <div className="row">
                    <label className="col-md-4">Hobby Tags</label>
                    <input className="col-md-8" type="text" name="hobby" placeholder="hobby"
                           value={hobby} onChange={this.handleChange}/>
                </div>
                <div className="row">
                    <label className="col-md-4">Aims Tags</label>
                    <input className="col-md-8" type="text" name="aims" placeholder="aims"
                           value={aims} onChange={this.handleChange}/>
                </div>
                <div className="row">
                    <label className="col-md-4">Job Tags</label>
                    <input className="col-md-8" type="text" name="job" placeholder="job"
                           value={job} onChange={this.handleChange}/>
                </div>
                <br/>
                <button className="btn btn-info" onClick={() => this.props.saveprofcallback()}>save</button>
                <br/>

            </div>
        );
    }
}

class MatcherResult extends React.Component {
    constructor(props) {
        super(props);
        this.findMatches = this.findMatches.bind(this);

        this.state = {
            matches: [
                {
                    name: "test",
                    ctn: "999"
                },
                {
                    name: "test2",
                    ctn: "9999"
                }

            ]
        };

    }

    findMatches() {
        var self = this;
        console.log("prep send " + JSON.stringify(this.props.prof));
        $.ajax({
            type: "POST",
            url: "http://localhost:8081/m",
            data: JSON.stringify(this.props.prof),
            // contentType: "text/plain; charset=utf-8"
            contentType: "application/json; charset=utf-8"
        }).then(function (data) {
            self.setState({matches: data});
        });
    }

    render() {
        var listItems = this.state.matches.map((profile) =>
            <li>{profile.name} ({profile.ctn})</li>
        );
        return (
            <div className="col-md-4">
                <h1>Matches for {this.props.prof.name} ({this.props.prof.ctn}) </h1>
                <ul>{listItems}</ul>
                <br/>
                <button className="btn btn-info" onClick={this.findMatches}>find!</button>
            </div>
        );
    }
}

class TableKVRow extends React.Component {
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

class AdminInfo extends React.Component {
    constructor(props) {
        super(props);
        this.handleChange = this.handleChange.bind(this);
    }


    handleChange(event) {
        //  event.preventDefault();

    }

    render() {
        var allCons = this.props.adminfo.allcons;
        var allConsRows = [];

        var globalTags = this.props.adminfo.globtags;
        var hobbyRows = [];
        var aimsRows = [];
        var jobRows = [];

        var ubu = this.props.adminfo.ubu;
        var ubuHobbyRows = [];
        var ubuAimsRows = [];
        var ubuJobRows = [];

        var similar = this.props.adminfo.similar;
        var similarHobbyRows = [];
        var similarAimsRows = [];
        var similarJobRows = [];

        Object.keys(allCons).forEach(function (ctn) {
            if (allCons.hasOwnProperty(ctn)) {
                allConsRows.push(<TableKVRow k={ctn} v={allCons[ctn]} key={ctn}/>);
            }
        });

        for (var category in globalTags) {
            if (ubu.hasOwnProperty(category)) {
                var tagObject = globalTags[category];
                for (var key in tagObject) {
                    if (tagObject.hasOwnProperty(key)) {
                        if (category == "hobby") {
                            hobbyRows.push(<TableKVRow k={key} v={tagObject[key]} key={key}/>);
                        }
                        if (category == "aims") {
                            console.log("kv aims" + key + tagObject[key]);
                            aimsRows.push(<TableKVRow k={key} v={tagObject[key]} key={key}/>);
                        }
                        if (category == "job") {
                            console.log("kv job" + key + tagObject[key]);

                            jobRows.push(<TableKVRow k={key} v={tagObject[key]} key={key}/>);
                        }
                    }
                }
            }
        }

        for (var category in ubu) {
            if (ubu.hasOwnProperty(category)) {
                var tagObject = ubu[category];
                for (var key in tagObject) {
                    if (tagObject.hasOwnProperty(key)) {
                        var tags = tagObject[key] ? tagObject[key].join(",") : "";

                        if (category == "hobby") {
                            ubuHobbyRows.push(<TableKVRow k={key} v={tags} key={key}/>);
                        }
                        if (category == "aims") {
                            ubuAimsRows.push(<TableKVRow k={key} v={tags} key={key}/>);
                        }
                        if (category == "job") {
                            ubuJobRows.push(<TableKVRow k={key} v={tags} key={key}/>);
                        }
                    }
                }
            }
        }

        for (var category in similar) {
            if (similar.hasOwnProperty(category)) {
                var tagObject = similar[category];
                for (var tag in tagObject) {
                    if (tagObject.hasOwnProperty(tag)) {
                        var ctns = tagObject[tag] ? tagObject[tag].join(",") : "";
                        console.log("kv similar " + tag + " - " + ctns);

                        if (category == "hobby") {
                            similarHobbyRows.push(<TableKVRow k={tag} v={ctns} key={tag}/>);
                        }
                        if (category == "aims") {
                            similarAimsRows.push(<TableKVRow k={tag} v={ctns} key={tag}/>);
                        }
                        if (category == "job") {
                            similarJobRows.push(<TableKVRow k={tag} v={ctns} key={tag}/>);
                        }
                    }
                }
            }
        }

        return (
            <div>
                <div className="col-md-4">
                    <div>
                        <div className="col-md-6">
                            <div>
                                <h3>Connection List</h3>
                                <div className="row">
                                    <input className="col-md-4" type="text" name="addcl" placeholder="1"
                                           onChange={this.handleChange}/>
                                    <button className="col-md-4 btn btn-info pull-right" onClick={() => this.props.xxx}>
                                        Add
                                    </button>
                                </div>
                                <br/>

                                <table className="table table-striped">
                                    <thead>
                                    <tr>
                                        <th>Ctn</th>
                                        <th>Unlock lvl</th>
                                    </tr>
                                    </thead>
                                    <tbody>{allConsRows}</tbody>
                                </table>

                            </div>
                        </div>
                        <div className="col-md-6">
                            <h3>All Tags</h3>
                            <div>
                                <button className="btn btn-default" type="button" data-toggle="collapse"
                                        data-target="#hobbycollapse" aria-expanded="false"
                                        aria-controls="hobbycollapse">
                                    Hobby
                                    <span className="caret"></span>
                                </button>
                                <div className="collapse" id="hobbycollapse">
                                    <table className="table table-striped">
                                        <thead>
                                        <tr>
                                            <th>Tag</th>
                                            <th>Count</th>
                                        </tr>
                                        </thead>
                                        <tbody>{hobbyRows}</tbody>
                                    </table>
                                </div>
                            </div>
                            <div>
                                <button className="btn btn-default" type="button" data-toggle="collapse"
                                        data-target="#aimscollapse" aria-expanded="false" aria-controls="aimscollapse">
                                    Aims
                                    <span className="caret"></span>
                                </button>
                                <div className="collapse" id="aimscollapse">
                                    <table className="table table-striped">
                                        <thead>
                                        <tr>
                                            <th>Tag</th>
                                            <th>Count</th>
                                        </tr>
                                        </thead>
                                        <tbody>{aimsRows}</tbody>
                                    </table>
                                </div>
                            </div>
                            <div>
                                <button className="btn btn-default" type="button" data-toggle="collapse"
                                        data-target="#jobcollapse" aria-expanded="false" aria-controls="jobcollapse">
                                    Job
                                    <span className="caret"></span>
                                </button>
                                <div className="collapse" id="jobcollapse">
                                    <table className="table table-striped">
                                        <thead>
                                        <tr>
                                            <th>Tag</th>
                                            <th>Count</th>
                                        </tr>
                                        </thead>
                                        <tbody>{jobRows}</tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div className="col-md-4">
                    <h3>U-by-U unlocks</h3>
                    <div className="row">
                        <div className="col-md-3">
                            <input className="form-control" type="text" name="unlockctn" placeholder="ctn"
                                   onChange={this.handleChange}/>
                        </div>
                        <div className="col-md-3">
                            <input className="form-control" type="text" name="unlockcat" placeholder="cat"
                                   onChange={this.handleChange}/>
                        </div>
                        <div className="col-md-3">
                            <input className="form-control" type="text" name="unlocktag" placeholder="tag"
                                   onChange={this.handleChange}/>
                        </div>
                        <div className="col-md-3">
                            <button className="btn btn-info" onClick={() => this.props.xxx}>Unlock</button>
                        </div>
                    </div>
                    <br/>

                    <div>
                        <button className="btn btn-default" type="button" data-toggle="collapse"
                                data-target="#hobbyubucollapse" aria-expanded="false"
                                aria-controls="hobbyubucollapse">
                            Hobby
                            <span className="caret"></span>
                        </button>
                        <div className="collapse" id="hobbyubucollapse">
                            <table className="table table-striped">
                                <thead>
                                <tr>
                                    <th>Ctn</th>
                                    <th>Tags</th>
                                </tr>
                                </thead>
                                <tbody>{ubuHobbyRows}</tbody>
                            </table>
                        </div>
                    </div>
                    <div>
                        <button className="btn btn-default" type="button" data-toggle="collapse"
                                data-target="#aimsubucollapse" aria-expanded="false"
                                aria-controls="aimsubucollapse">
                            Aims
                            <span className="caret"></span>
                        </button>
                        <div className="collapse" id="aimsubucollapse">
                            <table className="table table-striped">
                                <thead>
                                <tr>
                                    <th>Ctn</th>
                                    <th>Tags</th>
                                </tr>
                                </thead>
                                <tbody>{ubuAimsRows}</tbody>
                            </table>
                        </div>
                    </div>
                    <div>
                        <button className="btn btn-default" type="button" data-toggle="collapse"
                                data-target="#jobubucollapse" aria-expanded="false" aria-controls="jobubucollapse">
                            Job
                            <span className="caret"></span>
                        </button>
                        <div className="collapse" id="jobubucollapse">
                            <table className="table table-striped">
                                <thead>
                                <tr>
                                    <th>Ctn</th>
                                    <th>Tags</th>
                                </tr>
                                </thead>
                                <tbody>{ubuJobRows}</tbody>
                            </table>
                        </div>
                    </div>
                </div>
                <div className="col-md-4">
                    <h3>Similar On Tags</h3>
                    <div>
                        <button className="btn btn-default" type="button" data-toggle="collapse"
                                data-target="#hobbysimilarcollapse" aria-expanded="false"
                                aria-controls="hobbysimilarcollapse">
                            Hobby
                            <span className="caret"></span>
                        </button>
                        <div className="collapse" id="hobbysimilarcollapse">
                            <table className="table table-striped">
                                <thead>
                                <tr>
                                    <th>Tag</th>
                                    <th>Ctns</th>
                                </tr>
                                </thead>
                                <tbody>{similarHobbyRows}</tbody>
                            </table>
                        </div>
                    </div>
                    <div>
                        <button className="btn btn-default" type="button" data-toggle="collapse"
                                data-target="#aimssimilarcollapse" aria-expanded="false"
                                aria-controls="aimssimilarcollapse">
                            Aims
                            <span className="caret"></span>
                        </button>
                        <div className="collapse" id="aimssimilarcollapse">
                            <table className="table table-striped">
                                <thead>
                                <tr>
                                    <th>Tag</th>
                                    <th>Ctns</th>
                                </tr>
                                </thead>
                                <tbody>{similarAimsRows}</tbody>
                            </table>
                        </div>
                    </div>
                    <div>
                        <button className="btn btn-default" type="button" data-toggle="collapse"
                                data-target="#jobsimilarcollapse" aria-expanded="false"
                                aria-controls="jobsimilarcollapse">
                            Job
                            <span className="caret"></span>
                        </button>
                        <div className="collapse" id="jobsimilarcollapse">
                            <table className="table table-striped">
                                <thead>
                                <tr>
                                    <th>Tag</th>
                                    <th>Ctns</th>
                                </tr>
                                </thead>
                                <tbody>{similarJobRows}</tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

class ApiWrapper extends React.Component {
    constructor(props) {
        super(props);
        this.loadProfile = this.loadProfile.bind(this);
        this.updateProfile = this.updateProfile.bind(this);
        this.saveProfile = this.saveProfile.bind(this);

        this.state = {
            prof: {
                ctn: "999",
                name: "init",
                age: 999,
                city: "NNN",
                tags: {
                    hobby: ["xxx", "yyy"]
                }

            },
            adminfo: {
                allcons: [],
                globtags: {},
                ubu: {},
                similar: {}
            },

        };

    }

    loadProfile(ctn) {
        var self = this;
        console.log("loadProfile " + self.state.prof.ctn);
        $.ajax({
            url: "http://localhost:8080/profile/" + ctn
        }).then(function (data) {
            console.log(data);
            // self.updateProfile(data);
            self.setState({prof: data});
        });

        $.ajax({
            url: "http://localhost:8080/admin/info/" + ctn
        }).then(function (data) {
            console.log("admin/info");
            console.log(data);
            // self.updateProfile(data);
            self.setState({adminfo: data});
            console.log("admin/info state");
            console.log(self.state);

        });
    }


    saveProfile() {
        console.log("prep send " + JSON.stringify(this.state.prof));
        $.ajax({
            type: "POST",
            url: "http://localhost:8080/profile/" + this.state.prof.ctn,
            data: JSON.stringify(this.state.prof),
            contentType: "application/json; charset=utf-8"
        }).then(function (data) {
            console.log("ok");
        });
    }

    updateProfile(newprof) {
        jQuery.extend(this.state.prof, newprof);
        this.setState({prof: this.state.prof});
    }

    render() {
        return (
            <div>
                <div className="row">
                    <h2>Profile Setting</h2>
                    <ProfileInfo prof={this.state.prof} updateprofcallback={this.updateProfile}
                                 loadprofcallback={this.loadProfile}/>
                    <ProfileEditor prof={this.state.prof} adminfo={this.state.adminfo}
                                   updateprofcallback={this.updateProfile}
                                   saveprofcallback={this.saveProfile}/>
                    <MatcherResult prof={this.state.prof}/>
                </div>
                <div className="row">
                    <h2>Admin Settings</h2>
                    <AdminInfo adminfo={this.state.adminfo}/>
                </div>
            </div>
        );
    }
}

ReactDOM.render(
    // <EmployeeTable employees={EMPLOYEES} />, document.getElementById('root')
    <ApiWrapper />, document.getElementById('root')
);