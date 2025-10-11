module.exports = {
  extends: ['@commitlint/config-conventional'],
  // 헤더 파싱 규칙을 커스터마이즈하여 "#123 type(scope): subject" 형태를 허용
  parserPreset: {
    parserOpts: {
      // type + optional scope + subject + required trailing issue number in parentheses
      // e.g. "feat(scope): subject (#123)"
      headerPattern: /^(feat|fix|refac|test|chore|docs)(?:\(([a-z0-9_-]+)\))?: (.+) \(#\d+\)$/,
      headerCorrespondence: ['type', 'scope', 'subject']
    }
  },
  rules: {
    'header-max-length': [2, 'always', 72],
    'type-case': [2, 'always', 'lower-case'],
    'subject-max-length': [2, 'always', 50],
    'subject-full-stop': [2, 'never', '.'],
    // 허용 타입을 명시(프로젝트 사용 패턴 반영)
    'type-enum': [2, 'always', ['feat', 'fix', 'refac', 'test', 'chore', 'docs']]
  }
};
