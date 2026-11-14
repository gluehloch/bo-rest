# Eclipse / Start configuration

Main-Class:
```bash
de.betoffice.web.boot.BetofficeBootApplication
```

VM Arguments:
```bash
-Dspring.profiles.active=dev
```

GreenMail Standalone Variante starten:
```bash
java "-Dgreenmail.setup.test.all" "-Dgreenmail.users=foo:pwd@bar.com" -jar .\greenmail-standalone-2.1.15.jar
```