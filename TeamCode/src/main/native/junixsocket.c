// tiny adapter so we can use AF_UNIX (file) sockets in Java

#include <jni.h>
#include <stddef.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/un.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>

static void get_addr(struct sockaddr_un *addr, const char *path)
{
    addr->sun_family = AF_UNIX;

    strncpy(addr->sun_path, path, 100); // copy up to 100 characters (assume ASCII-only paths but break otherwise)
}

JNIEXPORT jint JNICALL Java_org_firstinspires_ftc_teamcode_util_websocket_UnixSocketServer__1create
        (JNIEnv *env, jobject obj, jstring path)
{
    jsize len = (*env)->GetStringLength(env, path);
    if (len >= 100)
    {
        return -1;
    }

    // create socket
    int sockfd = socket(AF_UNIX, SOCK_STREAM, 0);
    if (sockfd < 0)
    {
        return -2;
    }

    const char *in_path = (*env)->GetStringUTFChars(env, path, NULL);

    struct sockaddr_un addr;
    get_addr(&addr, in_path);

    if (bind(sockfd, (struct sockaddr *)&addr, sizeof(addr)) < 0)
    {
        (*env)->ReleaseStringUTFChars(env, path, in_path);
        return -3;
    }

    (*env)->ReleaseStringUTFChars(env, path, in_path);

    // mark as listening
    if (listen(sockfd, 1) < 0)
    {
        return -4;
    }

    return sockfd;
}

JNIEXPORT jint JNICALL Java_org_firstinspires_ftc_teamcode_util_websocket_UnixSocketServer__1accept
        (JNIEnv *env, jobject obj, jint server_fd)
{
    struct sockaddr_un client_addr;
    socklen_t addr_size = sizeof(client_addr);

    int fd = accept(server_fd, (struct sockaddr *)&client_addr, &addr_size);
    return fd;
}

JNIEXPORT jint JNICALL Java_org_firstinspires_ftc_teamcode_util_websocket_UnixSocketServer__1close
        (JNIEnv *env, jobject obj, jint fd)
{
    if (close(fd) < 0) return errno;
    return 0;
}

JNIEXPORT jint JNICALL Java_org_firstinspires_ftc_teamcode_util_websocket_UnixSocket__1create
        (JNIEnv *env, jobject obj)
{
    return socket(AF_UNIX, SOCK_STREAM, 0);
}

JNIEXPORT jint JNICALL Java_org_firstinspires_ftc_teamcode_util_websocket_UnixSocket__1connect
        (JNIEnv *env, jobject obj, jint fd, jstring path)
{
    jsize len = (*env)->GetStringLength(env, path);
    if (len >= 100)
    {
        return -1;
    }

    const char *in_path = (*env)->GetStringUTFChars(env, path, NULL);
    struct sockaddr_un addr;
    get_addr(&addr, in_path);

    if (connect(fd, (struct sockaddr *)&addr, sizeof(addr)) < 0)
    {
        (*env)->ReleaseStringUTFChars(env, path, in_path);
        return -2;
    }

    (*env)->ReleaseStringUTFChars(env, path, in_path);

    return 0;
}

JNIEXPORT jint JNICALL Java_org_firstinspires_ftc_teamcode_util_websocket_UnixSocket__1close
        (JNIEnv *env, jobject obj, jint fd)
{
    if (close(fd) < 0) return errno;
    return 0;
}

JNIEXPORT jint JNICALL Java_org_firstinspires_ftc_teamcode_util_websocket_UnixSocket__1recv
        (JNIEnv *env, jobject obj, jint fd, jbyteArray buf, jint off, jint len)
{
    jint data_len = (*env)->GetArrayLength(env, buf);
    jbyte *data = (*env)->GetByteArrayElements(env, buf, NULL);

    int n = len;
    if (n + off > data_len) n = data_len - off;

    ssize_t n_read = read(fd, data + off, n);

    (*env)->ReleaseByteArrayElements(env, buf, data, 0);

    return n_read;
}

JNIEXPORT jint JNICALL Java_org_firstinspires_ftc_teamcode_util_websocket_UnixSocket__1send
        (JNIEnv *env, jobject obj, jint fd, jbyteArray buf, jint off, jint len)
{
    jint data_len = (*env)->GetArrayLength(env, buf);
    jbyte *data = (*env)->GetByteArrayElements(env, buf, NULL);

    int n = len;
    if (n + off > data_len) n = data_len - off;

    ssize_t n_written = write(fd, data + off, n);

    (*env)->ReleaseByteArrayElements(env, buf, data, JNI_ABORT);

    return n_written;
}
