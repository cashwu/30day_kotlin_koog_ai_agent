package com.cashwu

import ai.koog.agents.memory.providers.AgentMemoryProvider
import ai.koog.agents.memory.providers.LocalFileMemoryProvider
import ai.koog.agents.memory.providers.LocalMemoryConfig
import ai.koog.agents.memory.storage.Aes256GCMEncryptor
import ai.koog.agents.memory.storage.EncryptedStorage
import ai.koog.rag.base.files.JVMFileSystemProvider
import java.util.Base64
import javax.crypto.KeyGenerator
import kotlin.io.path.Path

/**
 *
 * @author cash.wu
 * @since 2025/08/19
 *
 */
class SecureStorageManager {
    /**
     * 生成安全的加密金鑰
     */
    fun generateSecureKey(): String {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256)
        val secretKey = keyGenerator.generateKey()
        return Base64.getEncoder().encodeToString(secretKey.encoded)
    }

    /**
     * 建立加密的記憶體提供者
     */
    fun createSecureMemoryProvider(): AgentMemoryProvider {

        // 生成加密金鑰
        val encryptionKey = generateSecureKey()

        // 使用 AES-256-GCM 加密演算法
        val encryption = Aes256GCMEncryptor(encryptionKey)

        // 建立加密存儲
        val encryptedStorage = EncryptedStorage(
            fs = JVMFileSystemProvider.ReadWrite,
            encryption = encryption
        )

        // 返回安全的記憶體提供者
        return LocalFileMemoryProvider(
            config = LocalMemoryConfig("secure-memory"),
            storage = encryptedStorage,
            fs = JVMFileSystemProvider.ReadWrite,
            root = Path("secure/data")
        )
    }
}
