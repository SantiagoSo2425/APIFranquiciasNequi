package co.com.nequi.franquicias.mongo;

import co.com.nequi.franquicias.mongo.data.FranchiseData;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;

public interface MongoDBRepository extends ReactiveMongoRepository<FranchiseData, String>, ReactiveQueryByExampleExecutor<FranchiseData> {
}
