# Yeobaek-Python 개발 환경 설정 가이드

이 문서는 `yeobaek-python` 프로젝트의 Conda 개발 환경을 설정하는 방법을 안내합니다.

## 사전 요구 사항

- [Conda](https://docs.conda.io/projects/conda/en/latest/user-guide/install/index.html) (Anaconda 또는 Miniconda)가 설치되어 있어야 합니다.

## Conda 환경 생성

1.  이 프로젝트의 루트 디렉토리로 이동합니다.
2.  아래 명령어를 실행하여 `environment.yml` 파일로부터 `yeobaek_new`라는 이름의 Conda 환경을 생성합니다.

    ```bash
    conda env create -f environment.yml
    ```

3.  환경 생성이 완료되면, 아래 명령어로 새로운 환경을 활성화합니다.

    ```bash
    conda activate yeobaek_new
    ```

이제 프로젝트 실행에 필요한 모든 패키지가 설치된 `yeobaek_new` 환경이 준비되었습니다.
