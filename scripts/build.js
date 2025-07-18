const { spawn } = require('child_process');
const path = require('path');

const scriptDir = __dirname;
const args = process.argv.slice(2);

const extraModuleBuildTypes = ['plugin', 'cli', 'utils', 'scripts'];

for (const type of extraModuleBuildTypes) {
  if (args[0] === type) {
    const tsconfigPath = path.join(process.cwd(), type, 'tsconfig.json');
    if (require('fs').existsSync(tsconfigPath)) {
      args.unshift('--build', tsconfigPath);
      args.splice(1, 1);
    } else {
      console.log(`tsconfig.json not found in ${type}, skipping build for ${type}/`);
      process.exit(0);
    }
    break;
  }
}

if (process.stdout.isTTY && !process.env.CI && !process.env.EXPO_NONINTERACTIVE) {
  args.push('--watch');
}

const tscPath = path.resolve(__dirname, '..', 'node_modules', 'expo-module-scripts', 'bin', 'expo-module-tsc');

const child = spawn('npx', ['tsc', ...args], { stdio: 'inherit', shell: true });

child.on('exit', (code) => {
  process.exit(code);
});
