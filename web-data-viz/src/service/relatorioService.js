const { exec } = require("child_process");

function gerarRelatorio() {
  return new Promise((resolve, reject) => {
    exec(
      "java -jar ../simecom-report-app/target/comex-report-app-1.0.0-jar-with-dependencies.jar filtros-exemplo.json",

      (error, stdout, stderr) => {
        if (error) {
          reject(error);
          return;
        }

        resolve({
          stdout,
          stderr,
        });
      },
    );
  });
}

module.exports = {
  gerarRelatorio,
};
