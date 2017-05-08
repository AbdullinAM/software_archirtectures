# drop all tables
DROP TABLE IF EXISTS BUGREPORT_COMMENTS;
DROP TABLE IF EXISTS TICKET_ASSIGNEES;
DROP TABLE IF EXISTS TICKET_COMMENTS;
DROP TABLE IF EXISTS PROJECT_DEVELOPERS;
DROP TABLE IF EXISTS PROJECT_TESTERS;
DROP TABLE IF EXISTS COMMENTS;
DROP TABLE IF EXISTS BUGREPORT;
DROP TABLE IF EXISTS TICKET;
DROP TABLE IF EXISTS MILESTONE;
DROP TABLE IF EXISTS PROJECT;
DROP TABLE IF EXISTS MESSAGE;
DROP TABLE IF EXISTS USERS;

# create tables
CREATE TABLE USERS (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  login VARCHAR(100) NOT NULL,
  password CHAR(40)   # sha1 hash
);

CREATE TABLE MESSAGE (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user INT NOT NULL,
  message VARCHAR(1000) NOT NULL,
  FOREIGN KEY (user) REFERENCES USERS(id)
);

CREATE TABLE PROJECT (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  manager INT NOT NULL,
  teamleader INT NOT NULL,
  FOREIGN KEY (manager) REFERENCES USERS(id),
  FOREIGN KEY (teamleader) REFERENCES USERS(id)
);

CREATE TABLE BUGREPORT (
  id INT AUTO_INCREMENT PRIMARY KEY,
  project INT NOT NULL,
  creator INT NOT NULL,
  developer INT,
  status ENUM("OPENED", "ACCEPTED", "FIXED", "CLOSED") NOT NULL,
  creationTime DATETIME NOT NULL,
  description VARCHAR(1000) NOT NULL,
  FOREIGN KEY (project) REFERENCES PROJECT(id),
  FOREIGN KEY (creator) REFERENCES USERS(id),
  FOREIGN KEY (developer) REFERENCES USERS(id)
);

CREATE TABLE COMMENTS (
  id INT AUTO_INCREMENT PRIMARY KEY,
  time DATETIME NOT NULL,
  commenter INT NOT NULL,
  description VARCHAR(1000) NOT NULL,
  FOREIGN KEY (commenter) REFERENCES USERS(id)
);

CREATE TABLE MILESTONE (
  id INT AUTO_INCREMENT PRIMARY KEY,
  project INT NOT NULL,
  status ENUM("OPENED", "ACTIVE", "CLOSED") NOT NULL,
  startDate DATETIME NOT NULL,
  activeDate DATETIME,
  endDate DATETIME NOT NULL,
  closingDate DATETIME,
  FOREIGN KEY (project) REFERENCES PROJECT(id)
);

CREATE TABLE TICKET (
  id INT AUTO_INCREMENT PRIMARY KEY,
  milestone INT NOT NULL,
  creator INT NOT NULL,
  status ENUM("NEW", "ACCEPTED", "IN_PROGRESS", "FINISHED", "CLOSED") NOT NULL,
  creationTime DATETIME NOT NULL,
  task VARCHAR(1000),
  FOREIGN KEY (milestone) REFERENCES MILESTONE(id),
  FOREIGN KEY (creator) REFERENCES USERS(id)
);

# connection tables
CREATE TABLE BUGREPORT_COMMENTS (
  id INT AUTO_INCREMENT PRIMARY KEY,
  bugreport INT NOT NULL,
  commentid INT NOT NULL,
  FOREIGN KEY (bugreport) REFERENCES BUGREPORT(id),
  FOREIGN KEY (commentid) REFERENCES COMMENTS(id)
);

CREATE TABLE TICKET_ASSIGNEES (
  id INT AUTO_INCREMENT PRIMARY KEY,
  ticket INT NOT NULL,
  assignee INT NOT NULL,
  FOREIGN KEY (ticket) REFERENCES TICKET(id),
  FOREIGN KEY (assignee) REFERENCES USERS(id)
);

CREATE TABLE TICKET_COMMENTS (
  id INT AUTO_INCREMENT PRIMARY KEY,
  ticket INT NOT NULL,
  commentid INT NOT NULL,
  FOREIGN KEY (ticket) REFERENCES TICKET(id),
  FOREIGN KEY (commentid) REFERENCES COMMENTS(id)
);

CREATE TABLE PROJECT_DEVELOPERS (
  id INT AUTO_INCREMENT PRIMARY KEY,
  project INT NOT NULL,
  developer INT NOT NULL,
  FOREIGN KEY (project) REFERENCES PROJECT(id),
  FOREIGN KEY (developer) REFERENCES USERS(id)
);

CREATE TABLE PROJECT_TESTERS (
  id INT AUTO_INCREMENT PRIMARY KEY,
  project INT NOT NULL,
  tester INT NOT NULL,
  FOREIGN KEY (project) REFERENCES PROJECT(id),
  FOREIGN KEY (tester) REFERENCES USERS(id)
);

# inserting some data
INSERT INTO USERS(USERS.name, USERS.login, USERS.password) VALUES ("First User", "user1", SHA1("user"));
INSERT INTO USERS(USERS.name, USERS.login, USERS.password) VALUES ("Second User", "user2", SHA1("user"));
INSERT INTO USERS(USERS.name, USERS.login, USERS.password) VALUES ("Third User", "user3", SHA1("user"));
INSERT INTO USERS(USERS.name, USERS.login, USERS.password) VALUES ("Fourth User", "user4", SHA1("user"));
INSERT INTO USERS(USERS.name, USERS.login, USERS.password) VALUES ("Fifth User", "user5", SHA1("user"));

INSERT INTO PROJECT(PROJECT.name, PROJECT.manager, PROJECT.teamleader) VALUES(
  "First project",
  (SELECT (USERS.id) FROM USERS WHERE USERS.login = "user1"),
  (SELECT (USERS.id) FROM USERS WHERE USERS.login = "user2")
);

INSERT INTO PROJECT_DEVELOPERS(PROJECT_DEVELOPERS.project, PROJECT_DEVELOPERS.developer) VALUES (
  (SELECT (PROJECT.id) FROM PROJECT WHERE PROJECT.name = "First project"),
  (SELECT (USERS.id) FROM USERS WHERE USERS.login = "user3")
);

INSERT INTO PROJECT_TESTERS(PROJECT_TESTERS.project, PROJECT_TESTERS.tester) VALUES (
  (SELECT (PROJECT.id) FROM PROJECT WHERE PROJECT.name = "First project"),
  (SELECT (USERS.id) FROM USERS WHERE USERS.login = "user4")
);

INSERT INTO MILESTONE(MILESTONE.project, MILESTONE.status, MILESTONE.startDate, MILESTONE.endDate) VALUES (
  (SELECT (PROJECT.id) FROM PROJECT WHERE PROJECT.name = "First project"),
  "OPENED",
  NOW(),
  20171218131717
);

INSERT INTO TICKET(TICKET.milestone, TICKET.creator, TICKET.status, TICKET.creationTime, TICKET.task) VALUES (
  1,
  (SELECT (USERS.id) FROM USERS WHERE USERS.login = "user2"),
  "NEW",
  NOW(),
  "Do this"
);

INSERT INTO BUGREPORT(BUGREPORT.project, BUGREPORT.creator, BUGREPORT.status, BUGREPORT.creationTime, BUGREPORT.description) VALUES (
  (SELECT (PROJECT.id) FROM PROJECT WHERE PROJECT.name = "First project"),
  (SELECT (USERS.id) FROM USERS WHERE USERS.login = "user3"),
  "OPENED",
  NOW(),
  "New bug"
);